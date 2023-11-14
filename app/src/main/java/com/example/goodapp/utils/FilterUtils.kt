package com.example.goodapp.utils

import androidx.sqlite.db.SimpleSQLiteQuery

object FilterUtils {

    fun getFilteredQuery(filter: TasksFilterType): SimpleSQLiteQuery {
        val simpleQuery = StringBuilder().append("SELECT * FROM tasks ")
        when (filter) {
            TasksFilterType.COMPLETED_TASKS -> {
                simpleQuery.append("WHERE isCompleted = 1")
            }
            TasksFilterType.ACTIVE_TASKS -> {
                simpleQuery.append("WHERE isCompleted = 0")
            }
            else -> {
                // ALL_TASKS
            }
        }
        return SimpleSQLiteQuery(simpleQuery.toString())
    }
}