package com.technion.columbus.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.technion.columbus.map.GameScreen.Companion.GAME_MAP_TILES_HEIGHT
import com.technion.columbus.map.GameScreen.Companion.GAME_MAP_TILES_WIDTH
import com.technion.columbus.map.GameScreen.Companion.TILE_HEIGHT
import com.technion.columbus.map.GameScreen.Companion.TILE_WIDTH

class GameScreenCamera : OrthographicCamera() {

    companion object {
        const val SPEED_FACTOR = 0.25f
    }

    override fun translate(x: Float, y: Float) {
        val newX = SPEED_FACTOR * x
        val newY = SPEED_FACTOR * y
        super.translate(newX, newY)
        update()
        ensureBounds(position.x + newX, position.y + newY)
        update()
    }

    private fun ensureBounds(targetX: Float, targetY: Float) {
        val minX = zoom * viewportWidth / 2
        val maxX = GAME_MAP_TILES_WIDTH * TILE_WIDTH - minX

        val minY = zoom * viewportHeight / 2
        val maxY = GAME_MAP_TILES_HEIGHT * TILE_HEIGHT - minY

        position.set(
            minOf(
                maxX, maxOf(targetX, minX)
            ),
            minOf(
                maxY, maxOf(targetY, minY)
            ),
            0f
        )
    }
}