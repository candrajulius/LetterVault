package com.google.developers.lettervault.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.developers.lettervault.R
import com.google.developers.lettervault.util.TimePickerFragment
import java.text.SimpleDateFormat
import java.util.*

class AddLetterActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private lateinit var viewModel: AddLetterViewModel
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_letter)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.action_add_letter)

        val factory = AddViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this, factory)[AddLetterViewModel::class.java]

        viewModel.saved.observe(this) {
            if (it.getContentIfNotHandled() == true)
                onBackPressed()
            else {
                val message = getString(R.string.no_letter)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveLetter()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveLetter() {
        val subject =
            findViewById<TextInputEditText>(R.id.edit_subject).text.toString().trim()

         val day = findViewById<Spinner>(R.id.add_spinner_day).selectedItemPosition

        val startTime =
            findViewById<TextView>(R.id.add_text_start_time).text.toString().trim()

        val endTime = findViewById<TextView>(R.id.add_text_end_time).text.toString().trim()

        val content =
            findViewById<TextInputEditText>(R.id.edit_content).text.toString().trim()


        viewModel.save(subject = subject, content = content)




    }

    fun showStartTimePicker(view: View) {
        val startTimePicker = TimePickerFragment().show(
            supportFragmentManager, " startTime"
        )
        this.view = view
    }

    fun showEndTimePicker(view: View) {
        val endTimePicker = TimePickerFragment().show(
            supportFragmentManager, " endTime"
        )
        this.view = view
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calender = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (view.id) {
            R.id.add_start_time -> {
                findViewById<TextView>(R.id.add_text_start_time).text =
                    timeFormat.format(calender.time)
            }
            R.id.add_end_time -> {
                findViewById<TextView>(R.id.add_text_end_time).text =
                    timeFormat.format(calender.time)
            }
        }
    }

}