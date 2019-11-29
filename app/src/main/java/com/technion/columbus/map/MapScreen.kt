package com.technion.columbus.map

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class MapScreen : ApplicationAdapter() {

    private lateinit var batch: SpriteBatch
//    private lateinit var img: Texture
    private lateinit var map: ArduinoMap
    private lateinit var camera: OrthographicCamera

    override fun create() {
        batch = SpriteBatch()

        camera = OrthographicCamera()
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.update()

        map = ArduinoMap()
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (Gdx.input.isTouched) {
            camera.translate(Gdx.input.deltaX.toFloat(), Gdx.input.deltaY.toFloat())
            camera.update()
        }

        map.render(camera)
    }

    override fun dispose() {
        batch.dispose()
    }
}