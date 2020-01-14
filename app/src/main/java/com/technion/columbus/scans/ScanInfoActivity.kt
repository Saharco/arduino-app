package com.technion.columbus.scans

import android.os.Bundle
import android.view.MenuItem
import com.badlogic.gdx.backends.android.AndroidApplication
import com.technion.columbus.R
import com.technion.columbus.map.MapScreen
import com.technion.columbus.pojos.MapMatrix
import com.technion.columbus.pojos.Scan
import com.technion.columbus.utility.MAP_MATRIX
import com.technion.columbus.utility.SCAN
import kotlinx.android.synthetic.main.activity_scan_info.*

class ScanInfoActivity : AndroidApplication() {

    private val game = MapScreen()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_info)

        val scan = intent.getSerializableExtra(SCAN) as Scan
        val mapMatrix = intent.getSerializableExtra(MAP_MATRIX) as MapMatrix

        displayGameWindow()

        ScanCardViewHolder(scan_card)
                .populateUI(scan)

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayGameWindow() {
        val gameView = initializeForView(game)
        gameMapView.addView(gameView)
    }
}
