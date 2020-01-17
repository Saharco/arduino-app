package com.technion.columbus.map

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.view.MotionEvent
import android.widget.TextView
import androidx.annotation.StringRes
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
import kotlin.math.PI

class GameMapActivity : AndroidApplication() {

    companion object {
        const val TAG = "GameMapActivity"

        const val RPI_SEND_PORT = 50009
        const val RPI_RECV_PORT = 50011
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

    private lateinit var client: ClientThread

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

    override fun onStop() {
        if (::client.isInitialized)
            client.closeConnections()
        super.onStop()
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
        val ipAddress = getPreferences(Context.MODE_PRIVATE).getString(
            getString(R.string.preference_ip_address_key), DEFAULT_IP_ADDRESS
        )!!
        thread { client = ClientThread(ipAddress, RPI_SEND_PORT, RPI_RECV_PORT) }
    }

    private fun displayGameWindow() {
        game = GameScreen(
            playerName = chosenRobotTile!!,
            floorTileName = chosenFloorTile!!,
            wallTileName = chosenWallTile!!
        )
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
        displaySimpleDialog(R.string.dismiss_map_title, R.string.dismiss_map_message, {
            super.onBackPressed()
        }, {})
    }

    private fun displaySimpleDialog(
        @StringRes titleRes: Int, @StringRes messageRes: Int, positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        val title = TextView(this)
        title.setText(titleRes)
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
            .setMessage(messageRes)
            .setPositiveButton(android.R.string.yes) { _, _ -> positiveAction.invoke() }
            .setNegativeButton(android.R.string.no) { _, _ -> negativeAction.invoke() }
            .show()
        builder.create()
    }

    private fun uploadScan() {
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
        mapMatrix.robotX = GameScreen.GAME_MAP_TILES_WIDTH.toDouble() / 2
        mapMatrix.robotY = GameScreen.GAME_MAP_TILES_HEIGHT.toDouble() / 2
        mapMatrix.direction = 'd'
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

    inner class ClientThread(
        private val address: String, private val sendPort: Int,
        private val recvPort: Int
    ) {
        private lateinit var sendSocket: Socket
        private lateinit var recvSocket: Socket
        private var scanIsOnline = false
        private var isConnectedToSocket = false

        init {
            setGameListeners()
            connectToSocket(sendPort)
            configureButtons()
            connectToSocket(recvPort)
            thread { listen() }
        }

        private fun connectToSocket(port: Int) {
            try {
                val serverAddr = InetAddress.getByName(address)
                when (port) {
                    sendPort -> sendSocket = Socket(serverAddr, port)
                    recvPort -> recvSocket = Socket(serverAddr, port)
                }
                Log.d(TAG, "Connected to port $port")
                isConnectedToSocket = true
            } catch (e: UnknownHostException) {
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
                sendSocket.getOutputStream().write(action.toByteArray())
            } catch (e: IOException) {
                Log.d(TAG, "Caught an IOException while sending data")
            } catch (e: Exception) {
                Log.d(TAG, "Caught some other exception while sending data: " + e.cause)
            }
        }

        private fun setGameListeners() {
            disposeScanButton.setOnClickListener {
                onBackPressed()
            }

            finishScanButton.setOnClickListener {
                displaySimpleDialog(
                    R.string.finish_scan_title,
                    R.string.finish_scan_message,
                    ::uploadScan
                ) {}
            }
        }

        private fun configureButtons() {
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

            Log.d(TAG, "Finished configuring buttons")
        }

        private fun listen() {
            var i = 0
            scanIsOnline = true
            Log.d(TAG, "Started listening")
            while (scanIsOnline) {
                i++
                if (!isConnectedToSocket)
                    connectToSocket(recvPort)
                try {
                    val scanner = Scanner(recvSocket.getInputStream())
                    val input = scanner.nextLine()
                    val mapMatrix = parseInput(input.toByteArray())

                    this@GameMapActivity.mapMatrix = mapMatrix
                    game.setNewMap(mapMatrix)

                    Log.d(TAG, "Map No. $i")
                    Log.d(TAG, "$mapMatrix")
                } catch (e: Exception) {
                    Log.d(TAG, "Caught some exception while listening")
                    break
                }
                isConnectedToSocket = false
                recvSocket.close()
            }
            Log.d(TAG, "Finished listening")
        }

        fun closeConnections() {
            try {
                if (!sendSocket.isClosed)
                    sendSocket.close()
                if (!recvSocket.isClosed)
                    recvSocket.close()
            } catch (e: Exception) {
                Log.d(TAG, "Caught exception while closing connections")
            }
        }
    }

    private fun parseInput(input: ByteArray): MapMatrix {
        val width = (input.copyOfRange(0, 10)).toString(Charsets.UTF_8).toInt()
        val height = (input.copyOfRange(10, 20)).toString(Charsets.UTF_8).toInt()
        val robotX = (input.copyOfRange(
            20,
            40
        )).toString(Charsets.UTF_8).toDouble() + GameScreen.GAME_MAP_TILES_WIDTH / 2
        val robotY = (input.copyOfRange(
            40,
            60
        )).toString(Charsets.UTF_8).toDouble() + GameScreen.GAME_MAP_TILES_HEIGHT / 2
//        val direction = (input.copyOfRange(60, 80)).toString(Charsets.UTF_8).toDouble()
//        Log.d(TAG, "direction: $direction")

        val mapByteArray = input.copyOfRange(60, 60 + (width * height))
        val mapArray = CharArray(mapByteArray.size)
        for (i in mapArray.indices)
            mapArray[i] = mapByteArray[i].toChar()

        val detMapArray = convertToDeterministicArray(mapArray)
        val matrix = convertToMatrix(detMapArray, width, height)

//        val dirCode = getDirectionCode(direction)
        return MapMatrix(
            height,
            width,
            robotX,
            robotY,
            'd',
            matrix
        )  // TODO: Change direction to received one
    }

    private fun getDirectionCode(direction: Double): Char {
        val dirMod = direction % 2 * PI
        if (dirMod <= 1 / 4 * PI && dirMod > 7 / 4 * PI)
            return 'u'
        if (dirMod <= 3 / 4 * PI && dirMod > 1 / 4 * PI)
            return 'r'
        return if (dirMod <= 5 / 4 * PI && dirMod > 3 / 4 * PI)
            'd'
        else
            'l'
    }

    private fun convertToDeterministicArray(array: CharArray): CharArray {
        var obstaclesCounter = 0
        var floorCounter = 0
        var uncertainCounter = 0
        val detArray = CharArray(array.size)
        for (i in array.indices) {
            when {
                array[i].toInt() > 80 -> {
                    detArray[i] = '1'
                    obstaclesCounter++
                }
                array[i].toInt() == 0 -> {
                    detArray[i] = '?'
                    uncertainCounter++
                }
                else -> {
                    detArray[i] = '0'
                    floorCounter++
                }
            }
        }
        Log.d(
            TAG, "Obstacles: $obstaclesCounter, Floor tiles: $floorCounter, " +
                    "Uncertain tiles: $uncertainCounter"
        )
        return detArray
    }

    private fun convertToMatrix(array: CharArray, width: Int, height: Int): Array<CharArray> {
        val matrix: ArrayList<CharArray> = arrayListOf()
        for (i in 0 until height) {
            matrix.add(array.copyOfRange((height - i - 1) * width, ((height - i) * width)))
        }


        return matrix.toArray(arrayOf<CharArray>())
    }
}
