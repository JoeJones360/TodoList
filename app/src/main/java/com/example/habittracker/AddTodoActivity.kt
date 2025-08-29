package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTodoActivity : AppCompatActivity() {

    private val cal: Calendar = Calendar.getInstance()
    private var pickedDateUtcMs: Long = cal.timeInMillis
    private var pickedHour: Int = cal.get(Calendar.HOUR_OF_DAY)
    private var pickedMinute: Int = cal.get(Calendar.MINUTE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        val etName: EditText = findViewById(R.id.editTextName)
        val timePicker: TimePicker = findViewById(R.id.timePicker)
        val calendarView: CalendarView = findViewById(R.id.calendarView)
        val buttonSave: com.google.android.material.button.MaterialButton = findViewById(R.id.buttonSave)

        // TimePicker inline
        timePicker.setIs24HourView(true)
        timePicker.hour = pickedHour
        timePicker.minute = pickedMinute
        timePicker.setOnTimeChangedListener { _, h, m ->
            pickedHour = h
            pickedMinute = m
        }

        // Calendar inline
        calendarView.date = pickedDateUtcMs
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            pickedDateUtcMs = cal.timeInMillis
        }

        buttonSave.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                etName.error = "Please enter a task name"
                return@setOnClickListener
            }

            // Build strings for UI
            val dateStr = formatDate(pickedDateUtcMs) // e.g., "Tue, 17 Sep 2025"
            val timeStr = String.format(Locale.getDefault(), "%02d:%02d", pickedHour, pickedMinute)

            // Build exact trigger time in millis (local tz)
            val dueAt = Calendar.getInstance().apply {
                timeInMillis = pickedDateUtcMs
                set(Calendar.HOUR_OF_DAY, pickedHour)
                set(Calendar.MINUTE, pickedMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            setResult(RESULT_OK, Intent().apply {
                putExtra("TODO_NAME", name)
                putExtra("TODO_DATE", dateStr)
                putExtra("TODO_TIME", timeStr)
                putExtra("TODO_DUE_AT", dueAt)
            })
            finish()
        }
    }

    private fun formatDate(utcMs: Long): String {
        val fmt = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        return fmt.format(Date(utcMs))
    }
}
