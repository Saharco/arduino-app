package com.technion.columbus.pojos

import com.technion.columbus.utility.*
import java.io.Serializable
import java.util.*

/**
 * This data class represents an uploaded scan.
 * @param scanName: name of the scan
 * @param mapGridId: the id of the corresponding mapping for this scan, located at mapGrids/{mapGridId}
 * @param mapHeight: the amount of rows in this mapping
 * @param mapWidth: the amount of columns in this mapping
 * @param timestamp: the date in which the mapping was made
 * @param scanRadius: the scan radius that correlated with this mapping
 * @param floorTileName: the chosen floor tile for this mapping
 * @param wallTileName: the chosen wall tile for this mapping
 * @param robotTileName: the chosen robot tile for this mapping
 */
data class Scan(val scanName: String = "",
                val mapGridId: String? = null,
                val mapHeight: Int = 0,
                val mapWidth: Int = 0,
                val timestamp: Date? = null,
                val scanRadius: Float = 0f,
                val floorTileName: String = FLOOR_BLACK,
                val wallTileName: String = WALL_WOOD_1,
                val robotTileName: String = PLAYER_DOG) : Serializable