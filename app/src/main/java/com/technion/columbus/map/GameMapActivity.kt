package com.technion.columbus.map

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
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
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.util.*
import kotlin.concurrent.thread

class GameMapActivity : AndroidApplication() {

    companion object {
        const val TAG = "GameMapActivity"

        const val RPI_ADDRESS = "192.168.1.20"
        const val RPI_PORT = 50007
    }

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
        setButtons()
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

    private fun setButtons() {
        thread { ClientThread( RPI_ADDRESS, RPI_PORT).configure() }
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

    inner class ClientThread(address: String, port: Int) {
        lateinit var socket: Socket

        init {
            try {
                val serverAddr = InetAddress.getByName(address)
                socket = Socket(serverAddr, port)
                Log.d(TAG, "Connected to the socket")
            } catch(e: UnknownHostException) {
                Log.d(TAG, "Caught UnknownHostException")
                e.printStackTrace()
            } catch (e: IOException) {
                Log.d(TAG, "Caught IOException")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.d(TAG, "Caught some other exception")
                e.printStackTrace()
            }
        }

        private fun sendAction(action: String) {
            Log.d(TAG, "Action: $action")
            try {
                socket.getOutputStream().write(action.toByteArray())
            } catch (e: IOException) {
                Log.d(TAG, "Caught an IOException while sending data")
            } catch (e: Exception) {
                Log.d(TAG, "Caught some other exception while sending data: " + e.cause)
            }
        }

        private fun setGameListeners() {
            disposeScanButton.setOnClickListener {
                socket.close()
                onBackPressed()
            }

            finishScanButton.setOnClickListener {
                socket.close()
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

        fun configure() {
            Log.d(TAG, "Started configuring buttons")
            moveUp.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> thread { sendAction("f") }
                    MotionEvent.ACTION_UP -> thread { sendAction("n") }
                }
                true
            }
            moveLeft.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> thread { sendAction("l") }
                    MotionEvent.ACTION_UP -> thread { sendAction("n") }
                }
                true
            }
            moveRight.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> thread { sendAction("r") }
                    MotionEvent.ACTION_UP -> thread { sendAction("n") }
                }
                true
            }
            moveDown.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> thread { sendAction("b") }
                    MotionEvent.ACTION_UP -> thread { sendAction("n") }
                }
                true
            }

            setGameListeners()

            Log.d(TAG, "Finished configuring buttons")
        }
    }
}
