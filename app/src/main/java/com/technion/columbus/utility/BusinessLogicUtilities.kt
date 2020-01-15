package com.technion.columbus.utility

import com.technion.columbus.R
import com.technion.columbus.pojos.MapMatrix
import com.technion.columbus.pojos.MapUpload

/**
 * Converts a tile's string to that tile's drawable resource
 */
fun stringToTileResource(tileName: String): Int {
    return when (tileName) {
        PLAYER_DOG -> R.drawable.dog_front_idle
        PLAYER_CAT -> R.drawable.cat_front_idle
        PLAYER_CHICKEN -> R.drawable.chicken_front_idle
        PLAYER_HORSE -> R.drawable.horse_front_idle
        PLAYER_SHEEP -> R.drawable.sheep_front_idle

        WALL_WOOD_1 -> R.drawable.wall_tile

        FLOOR_BLACK -> R.drawable.floor_tile
        FLOOR_CHECKERED -> R.drawable.checkered_floor_tile

        else -> R.drawable.floor_tile
    }
}

/**
 * Converts a tile's drawable resource to that tile's name
 */
fun tileResourceToString(tileResource: Int): String {
    return when (tileResource) {
        R.drawable.dog_spin_animation -> PLAYER_DOG
        R.drawable.cat_spin_animation -> PLAYER_CAT
        R.drawable.chicken_spin_animation -> PLAYER_CHICKEN
        R.drawable.horse_spin_animation -> PLAYER_HORSE
        R.drawable.sheep_spin_animation -> PLAYER_SHEEP

        R.drawable.dog_front_idle -> PLAYER_DOG
        R.drawable.cat_front_idle -> PLAYER_CAT
        R.drawable.chicken_front_idle -> PLAYER_CHICKEN
        R.drawable.horse_front_idle -> PLAYER_HORSE
        R.drawable.sheep_front_idle -> PLAYER_SHEEP

        R.drawable.wall_tile -> WALL_WOOD_1

        R.drawable.floor_tile -> FLOOR_BLACK
        R.drawable.checkered_floor_tile -> FLOOR_CHECKERED

        else -> FLOOR_BLACK
    }
}

/**
 * This is a utility method for generating a random mapping
 */
fun createRandomDummyScan(): MapMatrix {

    val rows = 400
    val cols = 400
    val robotX = (50..rows).random()
    val robotY = (50..rows).random()
    val direction = listOf('d','l','r','u').random()

    val tiles: Array<CharArray> = Array(rows) {
        CharArray(cols) {
            // floor tile is 12 times more likely than wall tile
            if ((0..12).random() == 0) 1.toChar()
            else 0.toChar()
        }
    }

    return MapMatrix(rows, cols, robotX, robotY, direction, tiles)
}

/**
 * Converts a [MapMatrix] to a [MapUpload]
 */
fun mapMatrixToMapUpload(mapMatrix: MapMatrix): MapUpload {
    val tilesList = ArrayList(mapMatrix.tiles.map {
        ArrayList(it.toList())
    })

    return MapUpload(mapMatrix.rows, mapMatrix.cols, tilesList)
}

/**
 * Converts a [MapUpload] to a [MapMatrix], where the robot is facing down and placed at (0,0)
 */
fun mapUploadToMapMatrix(mapUpload: MapUpload): MapMatrix {
    val tilesGrid = mapUpload.tiles.map {
        it.toCharArray()
    }.toTypedArray()

    return MapMatrix(mapUpload.rows, mapUpload.cols, tiles = tilesGrid)
}