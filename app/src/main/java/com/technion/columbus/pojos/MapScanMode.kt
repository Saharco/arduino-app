package com.technion.columbus.pojos

import java.io.Serializable

enum class MapScanMode : Serializable {
    MODE_ILLEGAL,
    MODE_AUTOMATIC_GAME,
    MODE_MANUAL_GAME,
    MODE_AUTOMATIC_BLUEPRINT,
    MODE_MANUAL_BLUEPRINT
}