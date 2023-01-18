package com.qmd.jzen.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.qmd.jzen.database.entity.SongListInfo

/**
 * Create by OJun on 2022/2/19.
 *
 */
@Dao
interface SongListInfoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg songListInfo: SongListInfo)

    @Delete
    fun delete(vararg songListInfo: SongListInfo)

    @Query("delete from songlist")
    fun deleteAll()

    @Query("select * from songlist order by addTime")
    fun getAll(): LiveData<List<SongListInfo>>
}