package com.technion.columbus.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.technion.columbus.pojos.MapUpload

class ArduinoMap(mapFile: String = "20x20_meter_empty_map.tmx") : GameMap {

    private val tiledMap: TiledMap = TmxMapLoader().load(mapFile)
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

    fun asMapUpload(): MapUpload {
        //TODO: implement the parsing logic
        val tiles = ArrayList<ArrayList<Char>>()
        return MapUpload(height, width, tiles)
    }
}