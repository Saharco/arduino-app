package com.technion.columbus.map

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.badlogic.gdx.backends.android.AndroidApplication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.technion.columbus.R
import com.technion.columbus.main.MainActivity
import com.technion.columbus.pojos.MapMatrix
import com.technion.columbus.pojos.MapScanMode
import com.technion.columbus.pojos.Scan
import com.technion.columbus.utility.*
import it.emperor.animatedcheckbox.AnimatedCheckBox
import kotlinx.android.synthetic.main.activity_game_map.*
import java.util.*

class GameMapActivity : AndroidApplication() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var game: GameScreen
    private var mapMatrix = MapMatrix()

    private var scanName: String? = null
    private var mapScanMode: MapScanMode? = null
    private var scanRadius: Float? = null
    private var chosenFloorTile: String? = null
    private var chosenWallTile: String? = null
    private var chosenRobotTile: String? = null

    private var isRobotFollowed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_map)

        fetchIntentData()
        displayGameWindow()
        setGameListeners()
        configureFocusButton()

        //TODO: delete this when the real map data is being fetched correctly
        temporaryRandomButton.setOnClickListener {
            mapMatrix = createRandomDummyScan()
            game.setNewMap(mapMatrix)
        }
    }

    private fun configureFocusButton() {
        focusRobotButton.setOnClickListener {
            isRobotFollowed = if (isRobotFollowed) {
                game.followPlayer()
                (it as Button).setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_unfocus, 0, 0
                )
                isRobotFollowed.not()
            } else {
                game.unfollowPlayer()
                (it as Button).setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_focus, 0, 0
                )
                isRobotFollowed.not()
            }
        }
    }

    private fun setGameListeners() {
        disposeScanButton.setOnClickListener {
            onBackPressed()
        }

        finishScanButton.setOnClickListener {
            val progressDialog = ProgressDialog.show(
                this@GameMapActivity,
                getString(R.string.scan_upload_title),
                getString(R.string.loading_scan_upload_message)
            )

            progressDialog.isIndeterminate = true

            val mapGridId = "${System.currentTimeMillis()}${UUID.randomUUID()}"

            val storageRef = FirebaseStorage.getInstance()
                .reference
                .child("map_grids")
                .child(mapGridId)
            val bytes = mapMatrix.serialize()
            storageRef.putBytes(bytes)
                .addOnSuccessListener {
                    val scan = Scan(
                        scanName!!,
                        mapGridId,
                        mapMatrix.rows,
                        mapMatrix.cols,
                        Date(System.currentTimeMillis()),
                        scanRadius!!,
                        chosenFloorTile!!,
                        chosenWallTile!!,
                        chosenRobotTile!!
                    )
                    db.collection("scans")
                        .add(scan)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            displayFinishedScanDialog()
                        }
                }
        }

        //        val listener = object : ApplicationListener {
        //            override fun create() {}
        //
        //            override fun dispose() {}
        //
        //            override fun render() {}
        //
        //            override fun pause() {}
        //
        //            override fun resume() {}
        //
        //            override fun resize(width: Int, height: Int) {}
        //        }
    }

    private fun displayGameWindow() {
        game = GameScreen(playerName = chosenRobotTile!!)
        val gameView = initializeForView(game)
        gameMapView.addView(gameView)
    }

    private fun fetchIntentData() {
        scanName = intent.getStringExtra(SCAN_NAME)
        mapScanMode = intent.getSerializableExtra(MAP_SCAN_MODE) as MapScanMode
        scanRadius = intent.getFloatExtra(SCAN_RADIUS, 1f)
        chosenFloorTile = intent.getStringExtra(CHOSEN_FLOOR_TILE)
        chosenWallTile = intent.getStringExtra(CHOSEN_WALL_TILE)
        chosenRobotTile = intent.getStringExtra(CHOSEN_ROBOT_TILE)
    }

    private fun displayFinishedScanDialog() {
        val dialog = MaterialDialog(this@GameMapActivity)
        dialog.cornerRadius(20f)
            .noAutoDismiss()
            .customView(R.layout.fragment_upload_success)

        dialog.cancelOnTouchOutside(false)

        val dialogView = dialog.getCustomView()
        val uploadSuccessCheckBox =
            dialogView.findViewById<AnimatedCheckBox>(R.id.uploadSuccessCheckBox)
        uploadSuccessCheckBox.setChecked(checked = true, animate = true)

        dialog.positiveButton(R.string.upload_success_ok) {
            startActivity(Intent(this@GameMapActivity, MainActivity::class.java))
            dialog.dismiss()
        }

        dialog.onDismiss {
            startActivity(Intent(this@GameMapActivity, MainActivity::class.java))
        }

        dialog.show()
    }

    override fun onBackPressed() {
        val title = TextView(this)
        title.setText(R.string.dismiss_map_title)
        title.textSize = 20f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.colorText
            )
        )
        title.gravity = Gravity.CENTER
        title.setPadding(10, 40, 10, 24)
        val builder = AlertDialog.Builder(this)
        builder.setCustomTitle(title)
            .setMessage(R.string.dismiss_map_message)
            .setPositiveButton(android.R.string.yes) { _, _ -> super.onBackPressed() }
            .setNegativeButton(android.R.string.no) { _, _ -> }
            .show()
        builder.create()
        return
    }
}
