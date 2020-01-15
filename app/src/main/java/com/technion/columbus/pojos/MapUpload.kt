package com.technion.columbus.pojos

import java.io.Serializable

/**
 * This data class represents an uploaded mapping.
 * @param rows: the amount of rows in the mapping
 * @param cols: the amount of columns in the mapping
 * @param tiles: the content of the mapping itself. Valid values for the chars are: {0, 1, '?'} for "flor", "wall" and "unknown" respectively
 * @
 */
data class MapUpload(
    val rows: Int = 0,
    val cols: Int = 0,
    val tiles: ArrayList<ArrayList<Char>> = ArrayList()
) : Serializable