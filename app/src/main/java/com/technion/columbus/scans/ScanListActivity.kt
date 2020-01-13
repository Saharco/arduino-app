package com.technion.columbus.scans

import android.annotation.SuppressLint
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
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.technion.columbus.R
import com.technion.columbus.pojos.Scan
import com.technion.columbus.utility.hideKeyboard
import kotlinx.android.synthetic.main.activity_scan_list.*
import java.util.*

class ScanListActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Columbus/ScanList"
    }

    private lateinit var recyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()

    private lateinit var allScansAdapter: FirestoreRecyclerAdapter<Scan, ScanCardViewHolder>
    private lateinit var searchAdapter: RecyclerView.Adapter<ScanCardViewHolder>
    private val scansMap = HashMap<String, ArrayList<Scan>>()

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

        configureSearchView()
    }

    private fun configureSearchView() {
        searchView.background = ContextCompat.getDrawable(this, R.drawable.search_background)
        searchView.setBackIcon(getDrawable(R.drawable.ic_arrow_back_white_24dp))
        searchView.setTextColor(ContextCompat.getColor(this, R.color.colorText))

        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                // do nothing
            }

            override fun onSearchViewShown() {
                searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.i(TAG, "search bar #textSubmit: $query")
                        hideKeyboard(this@ScanListActivity)
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText == null || newText == "") {
                            showAllScans()
                        }
                        else
                            showFilteredScans(newText)
                        return false
                    }
                })
            }
        })
    }

    private fun showFilteredScans(query: String) {
        Log.d(TAG, "changing to filtered query")

        allScansAdapter.stopListening()

        val filteredList = scansMap.filterKeys { it.startsWith(query, ignoreCase = true) }
            .values
            .flatten()

        filteredList.sortedBy { it.timestamp } // observe that that Date implements Comparable!

        searchAdapter = object : RecyclerView.Adapter<ScanCardViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanCardViewHolder {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.scan_card, parent, false)
                return ScanCardViewHolder(itemView)
            }

            override fun getItemCount(): Int {
                return filteredList.size
            }

            override fun onBindViewHolder(holder: ScanCardViewHolder, position: Int) {
                val scan = filteredList[holder.adapterPosition]
                Log.d(TAG, "Binding chat with the following data: $scan")
                holder.bind(scan)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchAdapter
    }

    override fun onStart() {
        super.onStart()
        showAllScans()
    }

    override fun onStop() {
        super.onStop()
        searchView.closeSearch()
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
                holder.bind(scan)
                if (scansMap[scan.scanName] == null)
                    scansMap[scan.scanName] = ArrayList()
                scansMap[scan.scanName]!!.add(scan)
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

    override fun onBackPressed() {
        if (searchView.isSearchOpen)
            searchView.closeSearch()
        else
            super.onBackPressed()
    }
}
