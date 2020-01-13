package com.technion.columbus.pojos

import com.technion.columbus.R
import java.util.*

data class Scan(val scanName: String = "",
                val mapGridId: String? = null,
                val mapHeight: Int = 0,
                val mapWidth: Int = 0,
                val timestamp: Date? = null,
                val scanRadius: Float = 0f,
                val floorResId: Int = R.drawable.floor_tile,
                val wallResId: Int = R.drawable.wall_tile,
                val robotResId: Int = R.drawable.dog_front_idle)