package com.technion.columbus.scans

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.technion.columbus.R
import kotlinx.android.synthetic.main.activity_scan_info.*

class ScanInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_info)

        setSupportActionBar(scanInfoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}