package com.technion.columbus.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.technion.columbus.pojos.MapUpload

class ArduinoMap(mapFile: String = "20x20_meter_empty_map.tmx") : GameMap {

    companion object {
        const val TILE_WIDTH = 32
        const val TILE_HEIGHT = 32
    }

    private val tiledMap: TiledMap = TmxMapLoader().load(mapFile)
    private val tiledMapRenderer: OrthogonalTiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
    private val mapTiles = tiledMap.layers[0] as TiledMapTileLayer
    private val tiles =
        TextureRegion.split(Texture(Gdx.files.internal("interior.png")), TILE_WIDTH, TILE_HEIGHT)

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

    fun setMapTiles(newTiles: Array<CharArray>, rowStart: Int = 0, colStart: Int = 0) {
        newTiles.forEachIndexed { row, charArray ->
            charArray.forEachIndexed { col, char ->
                val (tileTexturePosX, tileTexturePosY) =
                    if (char == 1.toChar()) Pair(0, 0)
                    else Pair(1, 3)

                val cell = TiledMapTileLayer.Cell()
                cell.tile = StaticTiledMapTile(tiles[tileTexturePosX][tileTexturePosY])
                mapTiles.setCell(rowStart + row, colStart + col, cell)
            }
        }
    }
}