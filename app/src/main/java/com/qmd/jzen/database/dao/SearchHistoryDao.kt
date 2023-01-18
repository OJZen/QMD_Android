package com.qmd.jzen.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.qmd.jzen.database.entity.SearchHistory

/**
 * Create by OJun on 2022/1/25.
 *
 */
@Dao
interface SearchHistoryDao {
    @Query("select * from searchHistory order by time desc")
    fun getAll(): LiveData<List<SearchHistory>>

    @Delete
    fun delete(item: SearchHistory)

    @Query("delete from searchHistory")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: SearchHistory)

}