package com.example.goodapp.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodapp.data.Task
import com.example.goodapp.databinding.FragmentActiveTaskBinding
import com.example.goodapp.ui.HomeViewModel
import com.example.goodapp.ui.ViewModelFactory
import com.example.goodapp.utils.Event
import com.example.goodapp.utils.TasksFilterType
import com.google.android.material.snackbar.Snackbar

class ActiveTaskFragment : Fragment() {

    private var _binding: FragmentActiveTaskBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory.getInstance(requireContext())
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        _binding = FragmentActiveTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initAction()

        //All Task Filter
        homeViewModel.filter(TasksFilterType.ACTIVE_TASKS)

        taskAdapter = TaskAdapter { task, isChecked ->
            // Handle task checkbox changes here
            // Update the task's completion status in the ViewModel
            homeViewModel.completeTask(task, isChecked)
        }

        binding.allTaskContent.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.allTaskContent.rvTask.adapter = taskAdapter

        homeViewModel.snackbarText.observe(viewLifecycleOwner) {
            showSnackBar(it)
        }

        homeViewModel.tasks.observe(viewLifecycleOwner){
            showRecyclerView(it)
        }

    }

    private fun showRecyclerView(tasks: PagedList<Task>) {
        //Submit pagedList to adapter and update database when onCheckChange
        taskAdapter.submitList(tasks)
    }

    private fun showSnackBar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        Snackbar.make(
            binding.coordinatorLayout,
            getString(message),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun initAction() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = (viewHolder as TaskAdapter.TaskViewHolder).getTask
                homeViewModel.deleteTask(task)
            }

        })
        itemTouchHelper.attachToRecyclerView(binding.allTaskContent.rvTask)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}