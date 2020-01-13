package com.technion.columbus.pojos

import com.technion.columbus.R
import java.util.*

data class Scan(val scanName: String,
                val mapGridId: String,
                val mapHeight: Int,
                val mapWidth: Int,
                val timestamp: Date,
                val scanRadius: Float,
                val floorResId: Int = R.drawable.floor_tile,
                val wallResId: Int = R.drawable.wall_tile,
                val robotResId: Int = R.drawable.dog_front_idle)