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

class ArduinoMap(
    floorTileName: String,
    wallTileName: String,
    mapFile: String = "20x20_meter_empty_map.tmx"
) : GameMap {

    private val tiledMap: TiledMap = TmxMapLoader().load(mapFile)
    private val tiledMapRenderer: OrthogonalTiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
    private val floorTiles = tiledMap.layers[0] as TiledMapTileLayer
    private val wallTiles = tiledMap.layers[1] as TiledMapTileLayer

    override val width = (tiledMap.layers[0] as TiledMapTileLayer).width
    override val height = (tiledMap.layers[0] as TiledMapTileLayer).height
    override val layers = tiledMap.layers.size()

    private val floorTileCell = TiledMapTileLayer.Cell()
    private val wallTileCell = TiledMapTileLayer.Cell()

    init {
        floorTileCell.tile =
            StaticTiledMapTile(TextureRegion(Texture(Gdx.files.internal("floor_tiles/$floorTileName.png"))))
        wallTileCell.tile =
            StaticTiledMapTile(TextureRegion(Texture(Gdx.files.internal("wall_tiles/$wallTileName.png"))))
    }

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

    fun setMapTiles(newTiles: Array<CharArray>, rowStart: Int = 0, colStart: Int = 0) {
        newTiles.forEachIndexed { row, charArray ->
            charArray.forEachIndexed { col, char ->
                floorTiles.setCell(rowStart + row, colStart + col, floorTileCell)

                if (char == '1')
                    wallTiles.setCell(rowStart + row, colStart + col, wallTileCell)
            }
        }
    }
}