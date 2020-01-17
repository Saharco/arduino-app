package com.technion.columbus.pojos

import java.io.Serializable

/**
 * This data class is a parsed version of the information sent by the robot regarding the current mapping status
 * @param rows: the amount of rows in the mapping
 * @param cols: the amount of columns in the mapping
 * @param robotX: the X coordinate of the robot inside the map
 * @param robotY: the Y coordinate of the robot inside the map
 * @param direction: the direction the robot is currently facing. Valid values are: {'d', 'l', 'r', 'u'} for "down", "left", "right" and "up" respectively
 * @param tiles: the content of the mapping itself. Valid values for the chars are: {0, 1, '?'} for "flor", "wall" and "unknown" respectively
 */
data class MapMatrix(
    val rows: Int = 0,
    val cols: Int = 0,
    var robotX: Double = 0.0,
    var robotY: Double = 0.0,
    var direction: Char = 'd',
    val tiles: Array<CharArray> = emptyArray()
) : Serializable {

    companion object {
        fun deserialize(bytes: ByteArray): MapMatrix {
            return com.technion.columbus.utility.deserialize<MapMatrix>(bytes)
        }
    }

    fun serialize(): ByteArray {
        return com.technion.columbus.utility.serialize(this)
    }

    override fun toString(): String {
        return "MapMatrix(rows=$rows, cols=$cols, robotX=$robotX, robotY=$robotY, " +
        "direction: $direction, map size: ${tiles.size}x${tiles[0].size})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapMatrix

        if (rows != other.rows) return false
        if (cols != other.cols) return false
        if (robotX != other.robotX) return false
        if (robotY != other.robotY) return false
        if (direction != other.direction) return false
        if (!tiles.contentDeepEquals(other.tiles)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + robotX.hashCode()
        result = 31 * result + robotY.hashCode()
        result = 31 * result + direction.hashCode()
        result = 31 * result + tiles.contentDeepHashCode()
        return result
    }
}