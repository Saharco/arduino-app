package com.technion.columbus.map

import com.badlogic.gdx.graphics.OrthographicCamera

interface GameMap {

    val width: Int
    val height: Int
    val layers: Int

    fun render(camera: OrthographicCamera)

    fun dispose()

    fun getTileTypeByCoordinate(layer: Int, col: Int, row: Int): TileType?

    fun getTileTypeByPixelLocation(layer: Int, x: Float, y: Float): TileType? {
        return getTileTypeByCoordinate(
            layer,
            (x / TileType.TILE_SIZE).toInt(),
            (y / TileType.TILE_SIZE).toInt()
        )
    }
}