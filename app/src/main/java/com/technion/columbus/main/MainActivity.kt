package com.technion.columbus.main

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import com.technion.columbus.R
import com.technion.columbus.map.GameMapActivity
import com.technion.columbus.map.SocketsHandler
import com.technion.columbus.map.SocketsHolder.Companion.recvSocket
import com.technion.columbus.map.SocketsHolder.Companion.sendSocket
import com.technion.columbus.scans.ScanListActivity
import com.technion.columbus.utility.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.Socket
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val CHECK_CONNECTION_INTERVAL = 2000L
    }

    private lateinit var floorSlotPicker: TileSlotPicker
    private lateinit var wallSlotPicker: TileSlotPicker
    private lateinit var robotSlotPicker: TileSlotPicker

    private var loadingGameProgressDialog: ProgressDialog? = null

    private var connectionStatus = false
    private var validationHandler = Handler()
    private var configurationDialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))

        scanButton.setOnClickListener {
            displayConfigurationDialog()
        }
        previousScansButton.setOnClickListener {
            startActivity(Intent(this, ScanListActivity::class.java))
        }

        configureTileSlotPickers()
        configureSettingsButton()
    }

    override fun onStart() {
        super.onStart()
        connectionStatus = false
        val ipAddress = getPreferences(Context.MODE_PRIVATE).getString(
            getString(R.string.preference_ip_address_key), DEFAULT_IP_ADDRESS
        )!!
        connectSockets(ipAddress)
    }

    private fun configureSettingsButton() {
        settingsButton.setOnClickListener {
            val prevIpAddress = getPreferences(Context.MODE_PRIVATE).getString(
                getString(R.string.preference_ip_address_key), DEFAULT_IP_ADDRESS
            )

            val dialog = MaterialDialog(this@MainActivity).show {
                title(res = R.string.settings_title)
                input(
                    prefill = prevIpAddress,
                    maxLength = 15,
                    waitForPositiveButton = false
                ) { dialog, text ->
                    val inputField = dialog.getInputField()
                    val isValid = isValidIpAddress(text)

                    inputField.error = if (isValid) null else getString(R.string.error_invalid_ip)
                    dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
                }
                positiveButton(R.string.settings_positive_button) { dialog ->
                    val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return@positiveButton
                    with(sharedPref.edit()) {
                        putString(
                            getString(R.string.preference_ip_address_key),
                            dialog.getInputField().text.toString()
                        )
                        apply()

                        Toast.makeText(
                            this@MainActivity,
                            R.string.toast_ip_updated,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                negativeButton(R.string.settings_negative_button)
            }

            val inputField: EditText = dialog.getInputField()
            inputField.setBackgroundColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    android.R.color.transparent
                )
            )
        }
    }

    private fun isValidIpAddress(text: CharSequence): Boolean {
        return try {
            if (text.isEmpty()) {
                return false
            }
            val parts = text.split(".")
            if (parts.size != 4) {
                return false
            }
            for (s in parts) {
                val i = s.toInt()
                if (i < 0 || i > 255) {
                    return false
                }
            }
            !text.endsWith(".")
        } catch (nfe: NumberFormatException) {
            false
        }
    }

    private fun configureTileSlotPickers() {
        val floors = listOf(
            R.drawable.tile_floor_black,
            R.drawable.tile_floor_checkered,
            R.drawable.tile_floor_chess,
            R.drawable.tile_floor_dirt,
            R.drawable.tile_floor_grass,
            R.drawable.tile_floor_molten_rock,
            R.drawable.tile_floor_stone,
            R.drawable.tile_floor_wooden_plank,
            R.drawable.tile_floor_pale_blue_rough
        )

        val walls = listOf(
            R.drawable.tile_wall_wood_1,
            R.drawable.tile_wall_flower_pot,
            R.drawable.tile_wall_barrel,
            R.drawable.tile_wall_small_bush,
            R.drawable.tile_wall_large_bush,
            R.drawable.tile_wall_lava,
            R.drawable.tile_wall_brick,
            R.drawable.tile_wall_rock,
            R.drawable.tile_wall_blue_flowers
        )

        val robots = listOf(
            R.drawable.dog_spin_animation,
            R.drawable.cat_spin_animation,
            R.drawable.chicken_spin_animation,
            R.drawable.horse_spin_animation,
            R.drawable.sheep_spin_animation,
            R.drawable.little_boy_spin_animation,
            R.drawable.little_girl_spin_animation,
            R.drawable.old_man_spin_animation,
            R.drawable.old_lady_spin_animation
        )

        floorSlotPicker = TileSlotPicker(
            floorTileBox,
            getString(R.string.floor_slot_picker),
            floors
        )
        wallSlotPicker = TileSlotPicker(
            wallTileBox,
            getString(R.string.wall_slot_picker),
            walls
        )
        robotSlotPicker = TileSlotPicker(
            robotTileBox,
            getString(R.string.robot_slot_picker),
            robots,
            isAnimated = true
        )
    }

    private fun displayConfigurationDialog() {
        configurationDialog = MaterialDialog(this@MainActivity)
        configurationDialog!!.cornerRadius(20f)
            .title(R.string.scan_configuration_title)
            .noAutoDismiss()
            .customView(R.layout.fragment_scan_configuration, scrollable = true)

        val dialogView = configurationDialog!!.getCustomView()

        val connectionStatusImage = dialogView.findViewById<ImageView>(R.id.connectionStatusImage)
        val connectionStatusText = dialogView.findViewById<TextView>(R.id.connectionStatusText)
        connectionStatusImage.setImageResource(R.drawable.ic_disconnected)
        connectionStatusText.setText(R.string.scan_dialog_disconnected)

        val scanName = dialogView.findViewById<EditText>(R.id.scanName)

        val controlModeAutomatic = dialogView.findViewById<RadioButton>(R.id.controlModeAutomatic)
        val controlModeManual = dialogView.findViewById<RadioButton>(R.id.controlModeManual)

//        val mapModeGame = dialogView.findViewById<RadioButton>(R.id.mapModeGame)
//        val mapModeBlueprint = dialogView.findViewById<RadioButton>(R.id.mapModeBlueprint)

        val automaticModeOptions =
            dialogView.findViewById<ConstraintLayout>(R.id.automaticModeOptions)

        val scanRadiusPicker = dialogView.findViewById<TickerView>(R.id.scanRadiusPicker)
        val scanRadiusSeekBar = dialogView.findViewById<SeekBar>(R.id.scanRadiusSeekBar)

        configurationDialog?.positiveButton {
            val name = scanName.text.trim()
            if (name.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.scan_error_no_name),
                    Toast.LENGTH_LONG
                ).show()
                return@positiveButton
            }

//            var mapScanMode = MapScanMode.MODE_ILLEGAL
//            if (controlModeAutomatic.isChecked && mapModeGame.isChecked) {
//                mapScanMode = MapScanMode.MODE_AUTOMATIC_GAME
//            } else if (controlModeAutomatic.isChecked && mapModeBlueprint.isChecked) {
//                mapScanMode = MapScanMode.MODE_AUTOMATIC_BLUEPRINT
//            } else if (controlModeManual.isChecked && mapModeGame.isChecked) {
//                mapScanMode = MapScanMode.MODE_MANUAL_GAME
//            } else if (controlModeManual.isChecked && mapModeGame.isChecked) {
//                mapScanMode = MapScanMode.MODE_MANUAL_BLUEPRINT
//            }
//
//            if (mapScanMode == MapScanMode.MODE_ILLEGAL) {
//                Toast.makeText(
//                    this@MainActivity,
//                    getString(R.string.scan_error_no_map_scan_mode),
//                    Toast.LENGTH_LONG
//                ).show()
//                return@positiveButton
//            }

            if (!connectionStatus) {
                Toast.makeText(
                    this@MainActivity.applicationContext, "No connection with Columbus",
                    Toast.LENGTH_LONG
                ).show()
                return@positiveButton
            }

            val scanRadius = seekBarProgressToScanRadius(scanRadiusSeekBar.progress).toFloat()

            val startGameIntent = Intent(this@MainActivity, GameMapActivity::class.java)
            startGameIntent.putExtra(SCAN_NAME, name.toString())
//            startGameIntent.putExtra(MAP_SCAN_MODE, mapScanMode)
            startGameIntent.putExtra(SCAN_RADIUS, scanRadius)
            startGameIntent.putExtra(
                CHOSEN_FLOOR_TILE,
                tileResourceToString(floorSlotPicker.tileId)
            )
            startGameIntent.putExtra(CHOSEN_WALL_TILE, tileResourceToString(wallSlotPicker.tileId))
            startGameIntent.putExtra(
                CHOSEN_ROBOT_TILE,
                tileResourceToString(robotSlotPicker.tileId)
            )
            configurationDialog?.dismiss()

            loadingGameProgressDialog = ProgressDialog.show(
                this@MainActivity,
                getString(R.string.loading_scan_game_title),
                getString(R.string.loading_scan_game_message)
            )

            loadingGameProgressDialog!!.isIndeterminate = true

            startActivity(startGameIntent)
        }

        configurationDialog?.negativeButton {
            configurationDialog?.dismiss()
        }


        val handlerRunnable = object: Runnable {
            override fun run() {
                updateConnectionStatusUI(connectionStatusImage, connectionStatusText)
                validationHandler.postDelayed(this, CHECK_CONNECTION_INTERVAL)
            }
        }

        validationHandler.post(handlerRunnable)

        configurationDialog?.onDismiss {
            validationHandler.removeCallbacksAndMessages(null)
        }

        configurationDialog?.show {

            scanRadiusPicker.setCharacterLists(TickerUtils.provideNumberList())
            scanRadiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val distance = seekBarProgressToScanRadius(progress)
                    scanRadiusPicker.text = String.format("%.2f", distance)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })

            val distance = seekBarProgressToScanRadius(scanRadiusSeekBar.progress)
            scanRadiusPicker.text = String.format("%.2f", distance)

            controlModeAutomatic.setOnClickListener {
                automaticModeOptions.visibility = View.VISIBLE
            }

            controlModeManual.setOnClickListener {
                automaticModeOptions.visibility = View.GONE
            }
        }
    }

    private fun connectSockets(ipAddress: String) {
        thread {
            val initSockets =
                SocketsHandler(ipAddress, RPI_SEND_PORT, RPI_RECV_PORT)
            thread {
                while (!sendSocket.isConnected) {
                    Thread.sleep(CHECK_CONNECTION_INTERVAL)
                    sendSocket = initSockets.getSendSocket()
                }
            }
            thread {
                while (!recvSocket.isConnected) {
                    Thread.sleep(CHECK_CONNECTION_INTERVAL)
                    recvSocket = initSockets.getRecvSocket()
                }
            }
        }
    }

    private fun updateConnectionStatusUI(
        connectionStatusImage: ImageView,
        connectionStatusText: TextView
    ) {
        fun updateDisconnectedUI() {
            if (!isConnectionStatusChanged())
                return
            Log.d(TAG, "Disconnected. Updating connection indicators")
            runOnUiThread {
                connectionStatusImage.setImageResource(R.drawable.ic_disconnected)
                connectionStatusText.setText(R.string.scan_dialog_disconnected)
            }
            Log.d(TAG, "Finished Updating connection indicators")
        }

        fun updateConnectedUI() {
            if (!isConnectionStatusChanged())
                return
            Log.d(TAG, "Connected. Updating connection indicators")
            runOnUiThread {
                connectionStatusImage.setImageResource(R.drawable.ic_connected)
                connectionStatusText.setText(R.string.scan_dialog_connected)
            }
            Log.d(TAG, "Finished Updating connection indicators")
        }

        if (connectionStatus) {
            updateDisconnectedUI()
        } else {
            updateConnectedUI()
        }
    }

    private fun isConnectionStatusChanged(): Boolean {
        val prevConnectionStatus = connectionStatus
        updateConnectionStatus()
        if (connectionStatus != prevConnectionStatus)
            return true
        return false
    }

    private fun updateConnectionStatus() {
        fun isConnectionActive(socket: Socket): Boolean {
            return try {
                socket.inetAddress.isReachable(5000)
            } catch (e: Exception) {
                Log.d(TAG, "Got exception when checking connection with port ${socket.port}")
                return false
            }
        }

        fun checkConnectivity(): Boolean {
            var status = false
            thread { status = isConnectionActive(sendSocket) && isConnectionActive(recvSocket)}.join()
            return status
        }

        connectionStatus = checkConnectivity()
        Log.d(TAG, "connectionStatus is $connectionStatus")
    }

    override fun onStop() {
        super.onStop()
        configurationDialog?.dismiss()
        validationHandler.removeCallbacksAndMessages(null)
        loadingGameProgressDialog?.dismiss()
    }

    /**
     * Converts the SeekBar's progress value to an appropriate scan radius (minimum is 0.5 meters)
     */
    private fun seekBarProgressToScanRadius(progress: Int) = progress.toFloat() / 100 + 0.5
}
