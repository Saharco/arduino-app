package com.technion.columbus.map

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.technion.columbus.main.MainActivity.Companion.TAG

class MapScreen : ApplicationAdapter() {

    private lateinit var batch: SpriteBatch
    private lateinit var map: ArduinoMap
    private lateinit var camera: OrthographicCamera

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        batch = SpriteBatch()

        camera = OrthographicCamera()
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.update()

        map = ArduinoMap()
    }

    override fun render() {
        Gdx.gl.glClearColor(0.125f, 0.125f, 0.125f, 0.05f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (Gdx.input.isTouched) {
            camera.translate(-Gdx.input.deltaX.toFloat(), Gdx.input.deltaY.toFloat())
            camera.update()
        }

        if (Gdx.input.justTouched()) {
            val tilePosition = camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            val tileType = map.getTileTypeByPixelLocation(0, tilePosition.x, tilePosition.y)
            if (tileType != null) {
                Gdx.app.log(TAG, "clicked on tile: ${tileType.name}")
            }
        }

        map.render(camera)
    }

    override fun dispose() {
        batch.dispose()
        map.dispose()
    }
}