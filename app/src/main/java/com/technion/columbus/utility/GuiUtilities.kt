package com.technion.columbus.utility

import android.app.Activity
import android.os.Build
import android.view.WindowManager

/**
 * Changes the status bar's color (only works on API 21+)
 *
 * @param color: the selected color for the status bar
 */
fun changeStatusBarColor(activity: Activity, color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }
}