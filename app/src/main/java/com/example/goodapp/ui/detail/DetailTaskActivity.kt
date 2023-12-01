package com.example.goodapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.goodapp.databinding.ActivityDetailTaskBinding
import com.example.goodapp.ui.ViewModelFactory
import com.example.goodapp.utils.DateConverter
import com.example.goodapp.utils.TASK_ID

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailTaskBinding
    private lateinit var detailTaskViewModel: DetailTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val taskId = intent.getIntExtra(TASK_ID, 0)

        val factory = ViewModelFactory.getInstance(this)
        detailTaskViewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

        detailTaskViewModel.setTaskId(taskId)
        detailTaskViewModel.task.observe(this) { task ->
            task?.let {
                binding.detailEdTitle.setText(task.title)
                binding.detailEdDescription.setText(task.description)
                binding.detailEdDueDate.setText(DateConverter.convertMillisToString(task.dueDateMillis))
            }
        }

        binding.btnDeleteTask.setOnClickListener {
            detailTaskViewModel.deleteTask()
            finish()
        }
    }
}