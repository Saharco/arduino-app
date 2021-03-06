package com.technion.columbus.map

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.input.GestureDetector
import com.technion.columbus.pojos.MapMatrix
import com.technion.columbus.utility.FLOOR_BLACK
import com.technion.columbus.utility.PLAYER_DOG
import com.technion.columbus.utility.WALL_WOOD_1

class GameScreen(
    private val playerName: String = PLAYER_DOG,
    private val floorTileName: String = FLOOR_BLACK,
    private val wallTileName: String = WALL_WOOD_1,
    private val mapMatrix: MapMatrix? = null
) :
    ApplicationAdapter() {

    companion object {
        const val GAME_MAP_TILES_WIDTH = 400
        const val GAME_MAP_TILES_HEIGHT = 400

        const val TILE_HEIGHT = 32
        const val TILE_WIDTH = 32

        const val SCALE_FACTOR = 1.25f
        const val ANIMATION_FREQUENCY = 1 / 9f

        const val TRANSITION_FACTOR = .1f
    }

    private lateinit var map: ArduinoMap

    private lateinit var batch: SpriteBatch
    private lateinit var camera: OrthographicCamera
    private lateinit var texture: Texture

    private var animationFrames =
        com.badlogic.gdx.utils.Array<com.badlogic.gdx.utils.Array<TextureRegion>>(4)

    private var animations = com.badlogic.gdx.utils.Array<Animation<TextureRegion>>(4)
    private lateinit var downAnimation: Animation<TextureRegion>
    private lateinit var leftAnimation: Animation<TextureRegion>
    private lateinit var rightAnimation: Animation<TextureRegion>
    private lateinit var upAnimation: Animation<TextureRegion>

    private lateinit var currentAnimation: Animation<TextureRegion>

    var elapsedTime = 0f

    private var playerX: Float = (GAME_MAP_TILES_WIDTH * TILE_WIDTH).toFloat() / 2
    private var playerY: Float = (GAME_MAP_TILES_HEIGHT * TILE_HEIGHT).toFloat() / 2
    private var followingPlayer = false

    private var screenWidth: Float? = null
    private var screenHeight: Float? = null

    private var cameraWidth: Float? = null
    private var cameraHeight: Float? = null

    override fun create() {
        screenWidth = Gdx.graphics.width.toFloat()
        screenHeight = Gdx.graphics.height.toFloat()

        cameraWidth = screenWidth!! * SCALE_FACTOR
        cameraHeight = screenHeight!! * SCALE_FACTOR

        map = ArduinoMap(floorTileName, wallTileName)

        camera = GameScreenCamera()
        camera.setToOrtho(false, cameraWidth!!, cameraHeight!!)
        camera.position.set(playerX, playerY, 0f)
        camera.update()

        configurePlayerAnimation()

        if (mapMatrix != null)
            setNewMap(mapMatrix)

        Gdx.input.inputProcessor = GestureDetector(GameScreenGestureListener(camera))
    }

    private fun configurePlayerAnimation() {
        batch = SpriteBatch()
        texture = Texture("player_textures/$playerName.png")
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

        currentAnimation = downAnimation
    }

    override fun render() {
        super.render() // need this?

        elapsedTime += Gdx.graphics.deltaTime

        Gdx.gl.glClearColor(0.125f, 0.125f, 0.125f, 0.05f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (followingPlayer)
            transitionToPlayer()
        else
            moveCamera()

        map.render(camera)

        renderPlayer()
    }

    private fun moveCamera() {
        reverseCameraGesture()
    }

    private fun renderPlayer() {
        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(
            currentAnimation.getKeyFrame(elapsedTime, true) as TextureRegion,
            playerX,
            playerY,
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

        /*
        if (Gdx.input.justTouched()) {
            val tilePosition =
                camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            val tileType = map.getTileTypeByPixelLocation(0, tilePosition.x, tilePosition.y)
            if (tileType != null) {
                Gdx.app.log(TAG, "clicked on tile: ${tileType.name}")
            }
        }

         */
    }

    override fun dispose() {
        batch.dispose()
        map.dispose()
    }

    fun setNewMap(mapMatrix: MapMatrix) {
        val scanXToMapX = GAME_MAP_TILES_WIDTH / 2 - mapMatrix.rows / 2
        val scanYToMapY = GAME_MAP_TILES_HEIGHT / 2 - mapMatrix.cols / 2
        map.setMapTiles(mapMatrix.tiles, scanXToMapX, scanYToMapY)

        currentAnimation = when (mapMatrix.direction) {
            'd' -> downAnimation
            'l' -> leftAnimation
            'r' -> rightAnimation
            'u' -> upAnimation

            else -> downAnimation
        }

        playerX = mapMatrix.robotX.toFloat() * TILE_WIDTH.toFloat()
        playerY = mapMatrix.robotY.toFloat() * TILE_HEIGHT.toFloat()
    }

    fun followPlayer() {
        followingPlayer = true
        transitionToPlayer()
    }

    fun unfollowPlayer() {
        followingPlayer = false
    }

    private fun transitionToPlayer() {
        val position = camera.position
        position.x += (playerX - position.x) * TRANSITION_FACTOR
        position.y += (playerY - position.y) * TRANSITION_FACTOR
        camera.position.set(position)
        camera.update()
    }
}