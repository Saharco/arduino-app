package com.technion.columbus.pojos

import java.io.Serializable

/**
 * Representation for all possible mapping states + catch-all
 */
enum class MapScanMode : Serializable {
    MODE_ILLEGAL,
    MODE_AUTOMATIC_GAME,
    MODE_MANUAL_GAME,
    MODE_AUTOMATIC_BLUEPRINT,
    MODE_MANUAL_BLUEPRINT
}