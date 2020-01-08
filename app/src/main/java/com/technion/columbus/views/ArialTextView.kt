package com.technion.columbus.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.technion.columbus.R

class ArialTextView : TextView {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        setFont()
    }

    private fun setFont() {
        val font =
            ResourcesCompat.getFont(context, R.font.bubble)
        Log.d("Arial", "font is: $font")
        setTypeface(font, Typeface.NORMAL)
    }
}