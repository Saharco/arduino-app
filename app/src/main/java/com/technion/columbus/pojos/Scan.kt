package com.technion.columbus.pojos

import com.technion.columbus.utility.*
import java.io.Serializable
import java.util.*

data class Scan(val scanName: String = "",
                val mapGridId: String? = null,
                val mapHeight: Int = 0,
                val mapWidth: Int = 0,
                val timestamp: Date? = null,
                val scanRadius: Float = 0f,
                val floorTileName: String = FLOOR_BLACK,
                val wallTileName: String = WALL_WOOD_1,
                val robotTileName: String = PLAYER_DOG) : Serializable