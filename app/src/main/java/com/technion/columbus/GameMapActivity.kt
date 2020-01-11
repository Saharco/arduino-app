package com.technion.columbus

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.badlogic.gdx.backends.android.AndroidApplication
import com.technion.columbus.main.MainActivity
import com.technion.columbus.map.MapScreen
import kotlinx.android.synthetic.main.activity_game_map.*

class GameMapActivity : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_map)

        val gameFrame = gameMapView
        val game = MapScreen()
        val gameView = initializeForView(game)

        gameFrame.addView(gameView)

        disposeScanButton.setOnClickListener {
            onBackPressed()
        }

        finishScanButton.setOnClickListener {
            ProgressDialog.show(
                this@GameMapActivity,
                getString(R.string.loading_scan_upload_title),
                getString(R.string.loading_scan_upload_message)
            )
                .isIndeterminate = true

            //FIXME: need to create an AsyncTask that will: 1. stop listening for changes via network in the game. 2. get the tilemap from the game. 3. upload the tilemap to firebase. 4. start the next activity
            Handler().postDelayed({
                startActivity(Intent(this@GameMapActivity, MainActivity::class.java))
            }, 1250L) // this artificial loading is just a placeholder
        }

//
//        val listener = object : ApplicationListener {
//
//            private lateinit var batch: SpriteBatch
//            private lateinit var map: ArduinoMap
//            private lateinit var camera: OrthographicCamera
//
//            override fun create() {
//                Gdx.app.logLevel = Application.LOG_DEBUG
//                batch = SpriteBatch()
//
//                camera = OrthographicCamera()
//                camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
//                camera.update()
//
//                map = ArduinoMap()
//            }
//
//            override fun dispose() {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun render() {
//                Gdx.gl.glClearColor(0.125f, 0.125f, 0.125f, 0.05f)
//                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
//
//                if (Gdx.input.isTouched) {
//                    camera.translate(-Gdx.input.deltaX.toFloat(), Gdx.input.deltaY.toFloat())
//                    camera.update()
//                }
//
//                if (Gdx.input.justTouched()) {
//                    val tilePosition = camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
//                    val tileType = map.getTileTypeByPixelLocation(0, tilePosition.x, tilePosition.y)
//                    if (tileType != null) {
//                        Gdx.app.log(MainActivity.TAG, "clicked on tile: ${tileType.name}")
//                    }
//                }
//
//                map.render(camera)
//            }
//
//            override fun pause() {
//            }
//
//            override fun resume() {
//            }
//
//            override fun resize(width: Int, height: Int) {
//            }
//
//        }
    }

    override fun onBackPressed() {
        val title = TextView(this)
        title.setText(R.string.dismiss_map_title)
        title.textSize = 20f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(ContextCompat.getColor(this, R.color.colorText))
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
