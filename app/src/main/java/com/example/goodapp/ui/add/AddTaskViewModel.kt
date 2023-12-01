package com.example.goodapp.ui.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goodapp.data.Task
import com.example.goodapp.data.TaskRepository
import kotlinx.coroutines.launch

class AddTaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _insertResult = MutableLiveData<Long>()
    val insertResult : LiveData<Long> = _insertResult

    fun insertTask(task: Task) {
        try {
            viewModelScope.launch {
                _insertResult.value = taskRepository.insertTask(task)

                Log.d(TAG, "RESULT : ${_insertResult.value}")
            }
        } catch (e : Exception){
            Log.e(TAG, "EXCEPTION : $e")
        }
    }

    companion object {
        private const val TAG = "AddTaskViewModel"
    }

}