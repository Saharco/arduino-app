package com.technion.columbus.pojos

import java.io.Serializable

data class MapMatrix(
    val rows: Int = 0,
    val cols: Int = 0,
    val robotX: Int = 0,
    val robotY: Int = 0,
    val tiles: Array<CharArray> = emptyArray()
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapMatrix

        if (rows != other.rows) return false
        if (cols != other.cols) return false
        if (robotX != other.robotX) return false
        if (robotY != other.robotY) return false
        if (!tiles.contentDeepEquals(other.tiles)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + robotX
        result = 31 * result + robotY
        result = 31 * result + tiles.contentDeepHashCode()
        return result
    }
}