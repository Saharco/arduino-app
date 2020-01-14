package com.technion.columbus.pojos

import java.io.Serializable

data class MapMatrix(
    val rows: Int = 0,
    val cols: Int = 0,
    val tiles: ArrayList<Int> = ArrayList()
) : Serializable