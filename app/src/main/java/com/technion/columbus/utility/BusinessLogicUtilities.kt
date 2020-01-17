package com.technion.columbus.utility

import com.technion.columbus.R
import com.technion.columbus.pojos.MapMatrix
import com.technion.columbus.pojos.MapUpload
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

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
        PLAYER_LITTLE_BOY -> R.drawable.little_boy_front_idle
        PLAYER_LITTLE_GIRL -> R.drawable.little_girl_front_idle
        PLAYER_OLD_MAN -> R.drawable.old_man_front_idle
        PLAYER_OLD_LADY -> R.drawable.old_lady_front_idle

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
        R.drawable.little_boy_spin_animation -> PLAYER_LITTLE_BOY
        R.drawable.little_girl_spin_animation -> PLAYER_LITTLE_GIRL
        R.drawable.old_man_spin_animation -> PLAYER_OLD_MAN
        R.drawable.old_lady_spin_animation -> PLAYER_OLD_LADY

        R.drawable.dog_front_idle -> PLAYER_DOG
        R.drawable.cat_front_idle -> PLAYER_CAT
        R.drawable.chicken_front_idle -> PLAYER_CHICKEN
        R.drawable.horse_front_idle -> PLAYER_HORSE
        R.drawable.sheep_front_idle -> PLAYER_SHEEP
        R.drawable.little_boy_front_idle -> PLAYER_LITTLE_BOY
        R.drawable.little_girl_front_idle -> PLAYER_LITTLE_GIRL
        R.drawable.old_man_front_idle -> PLAYER_OLD_MAN
        R.drawable.old_lady_front_idle -> PLAYER_OLD_LADY


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
    val robotX = (50..cols).random().toDouble()
    val robotY = (50..rows).random().toDouble()
    val direction = listOf('d', 'l', 'r', 'u').random()

    val tiles: Array<CharArray> = Array(rows) {
        CharArray(cols) {
            // floor tile is 12 times more likely than wall tile
            if ((0..12).random() == 0) '1'
            else '0'
        }
    }

    return MapMatrix(rows, cols, robotX, robotY, direction, tiles)
}

/**
 * Converts a [MapMatrix] to a [MapUpload]
 */
fun MapMatrix.toMapUpload(): MapUpload {
    val tilesList = ArrayList<String>()
    for (row in 0 until this.rows) {
        for (col in 0 until this.cols) {
            tilesList.add(this.tiles[row][col].toString())
        }
    }

    return MapUpload(this.rows, this.cols, tilesList)
}

/**
 * Converts a [MapUpload] to a [MapMatrix], where the robot is facing down and placed at (0,0)
 */
fun MapUpload.toMapMatrix(): MapMatrix {
    val tilesGrid = Array(this.rows) { row ->
        CharArray(this.cols) { col ->
            this.tiles[this.rows * row + col][0]
        }
    }

    return MapMatrix(this.rows, this.cols, tiles = tilesGrid)
}

/**
 * Serialize given object of generic type [T] into [ByteArray]
 * @param obj: object to serialize
 * @see ObjectInputStream
 * @return the serialization result
 */
fun <T: Serializable> serialize(obj: T): ByteArray {
    val baos = ByteArrayOutputStream()
    val out = ObjectOutputStream(baos)
    out.writeObject(obj)
    out.flush()

    val result = baos.toByteArray()
    baos.close()

    return result
}

/**
 * Deserialize given [ByteArray] into object of generic type [T]
 * @param bytes: the bytes to deserialize
 * @return deserialized object of type [T]
 */
@Suppress("UNCHECKED_CAST")
fun <T: Serializable> deserialize(bytes: ByteArray): T {
    val bais = ByteArrayInputStream(bytes)
    val ois = ObjectInputStream(bais)

    val result = ois.readObject() as T
    ois.close()
    return result
}