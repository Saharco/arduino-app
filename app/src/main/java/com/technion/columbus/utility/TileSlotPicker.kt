package com.technion.columbus.utility

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.technion.columbus.R

class TileSlotPicker(
    private val context: Context,
    slotPicker: View,
    slotPickerName: String,
    private val tilesList: List<Int>
) {

    private val upButton = slotPicker.findViewById(R.id.up) as ImageView
    private val downButton = slotPicker.findViewById(R.id.down) as ImageView
    val tileBox = slotPicker.findViewById(R.id.tileBox) as ImageView
    private var slotIndex = 0

    init {
        val slotPickerTextView = slotPicker.findViewById(R.id.slotText) as TextView
        slotPickerTextView.text = slotPickerName
        setIndexTile()
        upButton.setOnClickListener {
            incrementSlotIndex()
        }
        downButton.setOnClickListener {
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

    private fun setIndexTile() =
        tileBox.setImageDrawable(ContextCompat.getDrawable(context, tilesList[slotIndex]))
}