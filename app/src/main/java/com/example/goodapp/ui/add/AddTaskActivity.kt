package com.example.goodapp.ui.add

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.goodapp.data.Task
import com.example.goodapp.databinding.ActivityAddTaskBinding
import com.example.goodapp.ui.ViewModelFactory
import com.example.goodapp.utils.DatePickerFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() , DatePickerFragment.DialogDateListener {
    private var dueDateMillis: Long = System.currentTimeMillis()
    private lateinit var taskViewModel : AddTaskViewModel
    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(this)
        taskViewModel = ViewModelProvider(this, factory)[AddTaskViewModel::class.java]

        addTaskHandler()
    }

    private fun addTaskHandler() {

        binding.buttonSaveTask.setOnClickListener {
            if (binding.addEdTitle.text.toString().isNotBlank()
                && binding.addEdDescription.text.toString().isNotBlank()
                && binding.addTvDueDate.text.toString().isNotBlank()
            ) {

                val newTask = Task(
                    title = binding.addEdTitle.text.toString(),
                    description = binding.addEdDescription.text.toString(),
                    dueDateMillis = dueDateMillis
                )

                taskViewModel.insertTask(newTask)

                finish()
            } else {
                Toast.makeText(
                    this,
                    "Oops! Some Fields are Empty!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun showDatePicker(view: View) {
        val dialogFragment = DatePickerFragment()
        dialogFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.addTvDueDate.text = dateFormat.format(calendar.time)

        dueDateMillis = calendar.timeInMillis
    }
}