package com.example.goodapp.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodapp.data.Task
import com.example.goodapp.databinding.FragmentCompletedTaskBinding
import com.example.goodapp.ui.HomeViewModel
import com.example.goodapp.ui.ViewModelFactory
import com.example.goodapp.utils.Event
import com.example.goodapp.utils.TasksFilterType
import com.google.android.material.snackbar.Snackbar

class CompletedTaskFragment : Fragment() {

    private var _binding: FragmentCompletedTaskBinding? = null

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
        _binding = FragmentCompletedTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initAction()

        //All Task Filter
        homeViewModel.filter(TasksFilterType.COMPLETED_TASKS)

        taskAdapter = TaskAdapter { task, isChecked ->
            // Handle task checkbox changes here
            // Update the task's completion status in the ViewModel or perform other operations
            homeViewModel.completeTask(task, isChecked)
        }

        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.adapter = taskAdapter

        homeViewModel.snackbarText.observe(viewLifecycleOwner) {
            showSnackBar(it)
        }

        homeViewModel.tasks.observe(viewLifecycleOwner, Observer(this::showRecyclerView))

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

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_settings -> {
//                val settingIntent = Intent(this, SettingsActivity::class.java)
//                startActivity(settingIntent)
//                true
//            }
//            R.id.action_filter -> {
//                showFilteringPopUpMenu()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    private fun showFilteringPopUpMenu() {
//        val view = findViewById<View>(R.id.action_filter) ?: return
//        PopupMenu(this, view).run {
//            menuInflater.inflate(R.menu.filter_tasks, menu)
//
//            setOnMenuItemClickListener {
//                taskViewModel.filter(
//                    when (it.itemId) {
//                        R.id.active -> TasksFilterType.ACTIVE_TASKS
//                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
//                        else -> TasksFilterType.ALL_TASKS
//                    }
//                )
//                true
//            }
//            show()
//        }
//    }

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
        itemTouchHelper.attachToRecyclerView(binding.rvTask)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}