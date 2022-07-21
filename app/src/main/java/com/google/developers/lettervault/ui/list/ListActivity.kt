package com.google.developers.lettervault.ui.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.developers.lettervault.R
import com.google.developers.lettervault.data.Letter
import com.google.developers.lettervault.ui.add.AddLetterActivity
import com.google.developers.lettervault.ui.detail.LetterDetailActivity
import com.google.developers.lettervault.ui.setting.SettingActivity
import com.google.developers.lettervault.util.DataViewModelFactory
import com.google.developers.lettervault.util.LETTER_ID
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.content_list.*

class ListActivity : AppCompatActivity() {

    private lateinit var viewModel: LetterViewModel
    private lateinit var rvLetter: RecyclerView
    private val letterAdapter: LetterAdapter by lazy {
        LetterAdapter(::onLetterClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = DataViewModelFactory(this)
        viewModel = ViewModelProviders.of(this, factory).get(LetterViewModel::class.java)

        setUpRecyclerView()
        letterList()
        setFabOnClickListener()
    }

    private fun setFabOnClickListener(){
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            startActivity(Intent(this@ListActivity,AddLetterActivity::class.java))
        }
    }


    private fun letterList() {
        viewModel.letters.observe(this) {
            Log.d("Letter", it.toString())
            letterAdapter.submitList(it)
        }
    }

    private fun onLetterClick(letter: Letter) {
        Intent(this@ListActivity,LetterDetailActivity::class.java).apply {
            putExtra(LETTER_ID,letter.id)
        }.also { startActivity(it) }
    }

    private fun setUpRecyclerView(){
        rvLetter = findViewById(R.id.rv_letters)
        rvLetter.apply {
            layoutManager = GridLayoutManager(this@ListActivity,2)
            adapter = letterAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.future, R.id.opened, R.id.all -> {
                item.isChecked = !item.isChecked
                val itemName = resources.getResourceEntryName(item.itemId)
                try {
                    viewModel.filter(itemName)
                } catch (e: IllegalArgumentException) {
                    Log.e(this.javaClass.name, "Invalid application state: ${e.message}")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
