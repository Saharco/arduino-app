package com.technion.columbus.scans

import android.os.Bundle
import android.view.MenuItem
import com.badlogic.gdx.backends.android.AndroidApplication
import com.technion.columbus.R
import com.technion.columbus.map.GameScreen
import com.technion.columbus.pojos.MapMatrix
import com.technion.columbus.pojos.MapUpload
import com.technion.columbus.pojos.Scan
import com.technion.columbus.utility.MAP_UPLOAD
import com.technion.columbus.utility.SCAN
import com.technion.columbus.utility.toMapMatrix
import kotlinx.android.synthetic.main.activity_scan_info.*

class ScanInfoActivity : AndroidApplication() {

    private lateinit var game: GameScreen
    private lateinit var scan: Scan
    private lateinit var mapMatrix: MapMatrix

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_info)

        scan = intent.getSerializableExtra(SCAN) as Scan
        mapMatrix = (intent.getSerializableExtra(MAP_UPLOAD) as MapUpload).toMapMatrix()

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
        game = GameScreen(scan.robotTileName)
        val gameView = initializeForView(game)
        gameMapView.addView(gameView)

        // define the fetched map to be the displayed map
        game.setNewMap(mapMatrix)
    }
}
