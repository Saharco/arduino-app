package com.technion.columbus.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

class ArduinoMap : GameMap {

    private val tiledMap: TiledMap = TmxMapLoader().load("map_example2.tmx")
    private val tiledMapRenderer: OrthogonalTiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
    override val width = (tiledMap.layers[0] as TiledMapTileLayer).width
    override val height = (tiledMap.layers[0] as TiledMapTileLayer).height
    override val layers = tiledMap.layers.size()

    override fun render(camera: OrthographicCamera) {
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()
    }

    override fun dispose() {
        tiledMap.dispose()
    }


    override fun getTileTypeByCoordinate(layer: Int, col: Int, row: Int): TileType? {
        val id = (tiledMap.layers[layer] as TiledMapTileLayer).getCell(col, row)
            ?.tile
            ?.id
            ?: return null

        return TileType[id]
    }

    fun asMatrix(): ArrayList<Int> {
        return ArrayList()
    }
}