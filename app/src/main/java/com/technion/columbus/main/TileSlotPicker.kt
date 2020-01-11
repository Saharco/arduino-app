package com.technion.columbus.main

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.technion.columbus.R
import com.technion.columbus.utility.makeHighlightedSnackbar
import kotlin.properties.Delegates

/**
 * Wraps the tile selection layout logic.
 * Constructs the slot picker logic: up & down buttons will properly rotate between the slot options
 * @param slotPicker: The tile selection view, with structure [R.layout.tile_selection]
 * @param slotPickerName: The required name for this tile slot picker, to be displayed on screen
 * @param tilesList: List of drawable resources for the slot's tiles
 * @param isAnimated: If true, will play [tilesList] drawables' animations
 */
class TileSlotPicker(
    slotPicker: View,
    slotPickerName: String,
    private val tilesList: List<Int>,
    private val isAnimated: Boolean = false
) {

    private val tileBox = slotPicker.findViewById(R.id.tileBox) as ImageView
    private var slotIndex = 0

    var tileId by Delegates.notNull<Int>()

    init {
        slotPicker.findViewById<TextView>(R.id.slotText).apply {
            text = slotPickerName
        }
        setIndexTile()

        if (tilesList.size == 1) {
            slotPicker.findViewById<ImageView>(R.id.up).setOnClickListener {
                makeHighlightedSnackbar(slotPicker, "More tiles will be added in the future!")
            }
            slotPicker.findViewById<ImageView>(R.id.down).setOnClickListener {
                makeHighlightedSnackbar(slotPicker, "More tiles will be added in the future!")
            }
        } else {
            slotPicker.findViewById<ImageView>(R.id.up).setOnClickListener {
                incrementSlotIndex()
            }
            slotPicker.findViewById<ImageView>(R.id.down).setOnClickListener {
                decrementSlotIndex()
            }
        }
    }

    /**
     * Increments the current tile to be displayed, in cyclic fashion, and then displays that tile
     */
    private fun incrementSlotIndex() {
        slotIndex = (slotIndex + 1).rem(tilesList.size)
        setIndexTile()
    }

    /**
     * Decrements the current tile to be displayed, in cyclic fashion, and then displays that tile
     */
    private fun decrementSlotIndex() {
        slotIndex = (slotIndex - 1).rem(tilesList.size)
        if (slotIndex < 0) slotIndex += tilesList.size
        setIndexTile()
    }

    /**
     * Displays the tile with resource [tilesList] [[slotIndex]]
     */
    private fun setIndexTile() {
        tileId = tilesList[slotIndex]
        tileBox.setBackgroundResource(tileId)
            if (isAnimated) {
            val animation = tileBox.background as AnimationDrawable
            animation.start()
        }
    }
}