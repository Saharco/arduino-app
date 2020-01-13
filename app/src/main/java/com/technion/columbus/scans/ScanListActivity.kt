package com.technion.columbus.scans

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.technion.columbus.R
import com.technion.columbus.pojos.Scan
import com.technion.columbus.utility.stringToTileResource
import kotlinx.android.synthetic.main.activity_scan_list.*
import java.text.SimpleDateFormat
import java.util.*

class ScanListActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Columbus/ScanList"
    }

    private lateinit var recyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private lateinit var allScansAdapter: FirestoreRecyclerAdapter<Scan, ScanCardViewHolder>
    private lateinit var searchAdapter: RecyclerView.Adapter<ScanCardViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_list)

        val viewManager = LinearLayoutManager(this)
        viewManager.orientation = LinearLayoutManager.VERTICAL

        recyclerView = findViewById<RecyclerView>(R.id.scan_list).apply {
            setHasFixedSize(true)
        }

        setSupportActionBar(previousScansToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        searchView.background = ContextCompat.getDrawable(this, R.drawable.search_background)
        searchView.setBackIcon(getDrawable(R.drawable.ic_arrow_back_white_24dp))
        searchView.setTextColor(ContextCompat.getColor(this, R.color.colorText))
    }

    override fun onStart() {
        super.onStart()
        showAllScans()
    }

    override fun onStop() {
        super.onStop()
        //searchView.closeSearch()
        allScansAdapter.stopListening()
    }

    private fun showAllScans() {
        Log.d(TAG, "Showing all previous scans")

        val adapterQuery = db.collection("scans")
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Scan>()
            .setQuery(adapterQuery, Scan::class.java)
            .build()
        allScansAdapter = createFirebaseAdapter(options)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = allScansAdapter
        allScansAdapter.startListening()
    }

    private fun createFirebaseAdapter(options: FirestoreRecyclerOptions<Scan>): FirestoreRecyclerAdapter<Scan, ScanCardViewHolder> {
        return object : FirestoreRecyclerAdapter<Scan, ScanCardViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ScanCardViewHolder {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.scan_card, parent, false)
                return ScanCardViewHolder(itemView)
            }

            @SuppressLint("SimpleDateFormat")
            override fun onBindViewHolder(
                holder: ScanCardViewHolder,
                position: Int,
                scan: Scan
            ) {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy")
                val hourFormat = SimpleDateFormat("HH:mm")
                val date = Date(scan.timestamp!!.time)

                holder.scanTitle.text = scan.scanName
                holder.scanDate.text = dateFormat.format(date)
                holder.scanTime.text = hourFormat.format(date)
                holder.floorTile.setImageResource(stringToTileResource(scan.floorTileName))
                holder.wallTile.setImageResource(stringToTileResource(scan.wallTileName))
                holder.robotTile.setImageResource(stringToTileResource(scan.robotTileName))
                holder.card.setOnClickListener {
                    val intent = Intent(it.context, ScanInfoActivity::class.java)
                    it.context.startActivity(intent)
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.previous_scans_menu, menu)
        searchView.setMenuItem(menu.findItem(R.id.search))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
