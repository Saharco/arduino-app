package com.technion.columbus.map

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.badlogic.gdx.backends.android.AndroidApplication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.model.value.ServerTimestampValue
import com.google.firestore.v1.DocumentTransform
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

    private val game = MapScreen()
    private var scanName: String? = null
    private var mapScanMode :MapScanMode? = null
    private var scanRadius: Float? = null
    private var chosenFloorTile: Int? = null
    private var chosenWallTile: Int? = null
    private var chosenRobotTile: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_map)

        fetchIntentData()
        displayGameWindow()
        setGameListeners()
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

            val map = MapMatrix(game.map.height, game.map.width, game.map.asMatrix())
            db.collection("mapGrids")
                .add(map) // check if this is fine
                .addOnSuccessListener {
                    val scan = Scan(
                        scanName!!,
                        it.id,
                        map.rows,
                        map.cols,
                        Date(System.currentTimeMillis()),
                        scanRadius!!,
                        chosenFloorTile!!,
                        chosenWallTile!!,
                        chosenRobotTile!!
                    )
                    db.collection("scans")
                        .add(scan) // check if this is fine
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
        val gameView = initializeForView(game)
        gameMapView.addView(gameView)
    }

    private fun fetchIntentData() {
        scanName = intent.getStringExtra(SCAN_NAME)
        mapScanMode = intent.getSerializableExtra(MAP_SCAN_MODE) as MapScanMode
        scanRadius = intent.getFloatExtra(SCAN_RADIUS, 1f)
        chosenFloorTile = intent.getIntExtra(CHOSEN_FLOOR_TILE, R.drawable.floor_tile)
        chosenWallTile = intent.getIntExtra(CHOSEN_WALL_TILE, R.drawable.wall_tile)
        chosenRobotTile = intent.getIntExtra(CHOSEN_ROBOT_TILE, R.drawable.dog_front_idle)
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
        title.setTextColor(ContextCompat.getColor(this,
            R.color.colorText
        ))
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
