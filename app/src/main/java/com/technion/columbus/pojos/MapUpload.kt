package com.technion.columbus.pojos

import java.io.Serializable

data class MapUpload(
    val rows: Int = 0,
    val cols: Int = 0,
    val tiles: ArrayList<ArrayList<Char>> = ArrayList()
) : Serializable