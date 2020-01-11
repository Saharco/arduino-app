package com.technion.columbus

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.technion.columbus.map.MapScreen
import kotlinx.android.synthetic.main.activity_game_map.*

class GameMapActivity : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_map)

        val gameFrame = gameMapView
        val game = initializeForView(MapScreen())

        gameFrame.addView(game)

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
}
