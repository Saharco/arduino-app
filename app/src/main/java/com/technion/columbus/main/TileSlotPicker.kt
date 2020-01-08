package com.technion.columbus.main

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.technion.columbus.R

class TileSlotPicker(
    private val context: Context,
    slotPicker: View,
    slotPickerName: String,
    private val tilesList: List<Int>,
    private val isAnimated: Boolean = false
) {

    val tileBox = slotPicker.findViewById(R.id.tileBox) as ImageView
    private var slotIndex = 0

    init {
        slotPicker.findViewById<TextView>(R.id.slotText).apply {
            text = slotPickerName
        }
        setIndexTile()

        slotPicker.findViewById<ImageView>(R.id.up).setOnClickListener {
            incrementSlotIndex()
        }
        slotPicker.findViewById<ImageView>(R.id.down).setOnClickListener {
            decrementSlotIndex()
        }
    }

    private fun incrementSlotIndex() {
        slotIndex = (slotIndex + 1).rem(tilesList.size)
        setIndexTile()
    }

    private fun decrementSlotIndex() {
        slotIndex = (slotIndex - 1).rem(tilesList.size)
        if (slotIndex < 0) slotIndex += tilesList.size
        setIndexTile()
    }

    private fun setIndexTile() {
        tileBox.setBackgroundResource(tilesList[slotIndex])
        if (isAnimated) {
            val animation = tileBox.background as AnimationDrawable
            animation.start()
        }
    }
}