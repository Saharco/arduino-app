package com.technion.columbus.map

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.Vector3
import com.technion.columbus.main.MainActivity.Companion.TAG

class GameScreen(private val playerName: String) : ApplicationAdapter(), InputProcessor {

    companion object {
        const val TILE_HEIGHT = 32
        const val TILE_WIDTH = 32

        const val SCALE_FACTOR = 1.4f
        const val ANIMATION_FREQUENCY = 1 / 9f
    }

    lateinit var map: ArduinoMap

    private lateinit var batch: SpriteBatch
    private lateinit var camera: OrthographicCamera
    private lateinit var texture: Texture

    private var animationFrames =
        com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Array<TextureRegion>>(4)

    private var animations = com.badlogic.gdx.utils.Array<Animation<TextureRegion>>(4)
    lateinit var downAnimation: Animation<TextureRegion>
    lateinit var leftAnimation: Animation<TextureRegion>
    lateinit var rightAnimation: Animation<TextureRegion>
    lateinit var upAnimation: Animation<TextureRegion>

    var elapsedTime = 0f

    private var screenWidth: Float? = null
    private var screenHeight: Float? = null

    private var cameraWidth: Float? = null
    private var cameraHeight: Float? = null

    override fun create() {
        screenWidth = Gdx.graphics.width.toFloat()
        screenHeight = Gdx.graphics.height.toFloat()

        cameraWidth = screenWidth!! * SCALE_FACTOR
        cameraHeight = screenHeight!! * SCALE_FACTOR

        camera = OrthographicCamera()
        camera.setToOrtho(false, cameraWidth!!, cameraHeight!!)
        camera.position.set(cameraWidth!! / 2, cameraHeight!! / 2, 0f)
        camera.update()

        map = ArduinoMap()

        configurePlayerAnimation()
    }

    private fun configurePlayerAnimation() {
        batch = SpriteBatch()
        texture = Texture("$playerName.png")
        val allFrames = TextureRegion.split(texture, TILE_WIDTH, TILE_HEIGHT)

        // --- temporary ---

        for (i in 0 until 4)
            animationFrames.add(com.badlogic.gdx.utils.Array(3))

        for (i in 0 until 4)
            for (j in 0 until 3)
                animationFrames[i].add(allFrames[i][j])

        animationFrames.forEach {
            animations.add(Animation(ANIMATION_FREQUENCY, it))
        }

        downAnimation = animations[0]
        leftAnimation = animations[1]
        rightAnimation = animations[2]
        upAnimation = animations[3]
    }

    override fun render() {
        super.render() // need this?

        elapsedTime += Gdx.graphics.deltaTime

        Gdx.gl.glClearColor(0.125f, 0.125f, 0.125f, 0.05f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        reverseCameraGesture()

        map.render(camera)

        renderPlayer()
    }

    private fun renderPlayer() {
        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(
            rightAnimation.getKeyFrame(elapsedTime, true) as TextureRegion,
            camera.position.x,
            camera.position.y,
            TILE_WIDTH / SCALE_FACTOR * 5,
            TILE_HEIGHT / SCALE_FACTOR * 5
        )
        batch.end()
    }

    private fun reverseCameraGesture() {
        if (Gdx.input.isTouched) {
            camera.translate(-Gdx.input.deltaX.toFloat(), Gdx.input.deltaY.toFloat())
            camera.update()
        }

        if (Gdx.input.justTouched()) {
            val tilePosition =
                camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            val tileType = map.getTileTypeByPixelLocation(0, tilePosition.x, tilePosition.y)
            if (tileType != null) {
                Gdx.app.log(TAG, "clicked on tile: ${tileType.name}")
            }
        }
    }

    override fun dispose() {
        batch.dispose()
        map.dispose()
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyTyped(character: Char): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun scrolled(amount: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyUp(keycode: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyDown(keycode: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}