package com.technion.columbus.utility

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar

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

/**
 * Shows a highlighted text in a snackbar.
 * Used mainly to notify the user about errors
 *
 * @param root: the context's root element (activity's root layout)
 * @param msg:  the message displayed in the snackbar
 */
fun makeHighlightedSnackbar(root: View, msg: String) {
    val snackbar = Snackbar.make(root, msg, Snackbar.LENGTH_SHORT)
//    val textView =
//        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
//    textView.setTextColor(Color.YELLOW)
    snackbar.show()
}

/**
 * Hides the virtual keyboard in the activity, if it is open
 *
 * @param activity: activity in which the keyboard should be hidden
 */
fun hideKeyboard(activity: Activity) {
    val imm = activity
        .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        // There is no view to pass the focus to, so we create a new view
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}