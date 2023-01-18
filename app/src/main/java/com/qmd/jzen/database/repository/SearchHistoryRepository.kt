package com.qmd.jzen.database.repository

import com.qmd.jzen.database.dao.SearchHistoryDao
import com.qmd.jzen.database.entity.SearchHistory

/**
 * Create by OJun on 2022/1/25.
 *
 */
class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {
    val allHistory = searchHistoryDao.getAll()

    fun delete(item: SearchHistory) {
        searchHistoryDao.delete(item)
    }

    fun insert(item: SearchHistory) {
        searchHistoryDao.insert(item)
    }

    fun deleteAll() {
        searchHistoryDao.deleteAll()
    }
}