package com.example.habittracker

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTodoActivity : AppCompatActivity() {

    private val cal = Calendar.getInstance()
    private var pickedDateMs = cal.timeInMillis
    private var pickedHour = cal.get(Calendar.HOUR_OF_DAY)
    private var pickedMinute = cal.get(Calendar.MINUTE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        val nameInput = findViewById<EditText>(R.id.editTextName)
        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val saveButton = findViewById<MaterialButton>(R.id.buttonSave)

        timePicker.setIs24HourView(true)
        timePicker.hour = pickedHour
        timePicker.minute = pickedMinute
        timePicker.setOnTimeChangedListener { _, h, m ->
            pickedHour = h
            pickedMinute = m
        }

        calendarView.date = pickedDateMs
        calendarView.setOnDateChangeListener { _, year, month, day ->
            pickedDateMs = Calendar.getInstance().apply {
                set(year, month, day)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) {
                nameInput.error = "Please enter a task name"
                return@setOnClickListener
            }

            val dueAt = Calendar.getInstance().apply {
                timeInMillis = pickedDateMs
                set(Calendar.HOUR_OF_DAY, pickedHour)
                set(Calendar.MINUTE, pickedMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            setResult(RESULT_OK, Intent().apply {
                putExtra("TODO_NAME", name)
                putExtra("TODO_DATE", formatDate(pickedDateMs))
                putExtra("TODO_TIME", String.format(Locale.getDefault(), "%02d:%02d", pickedHour, pickedMinute))
                putExtra("TODO_DUE_AT", dueAt)
            })
            finish()
        }
    }

    private fun formatDate(ms: Long): String =
        SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date(ms))
}
