package com.qmd.jzen.database.dao

import androidx.room.*
import com.qmd.jzen.database.entity.Music
import kotlinx.coroutines.flow.Flow

/**
 * Create by OJun on 2021/3/5.
 *
 */
@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg music: Music)

    @Delete
    fun delete(vararg music: Music)

    @Query("select * from FavoriteMusic order by rowId desc")
    fun getAll(): Flow<List<Music>>

    @Query("select musicId from FavoriteMusic")
    fun getAllMusicId(): List<String>

    @Query("delete from FavoriteMusic")
    fun deleteAll()

    @Query("select * from FavoriteMusic where musicId=:musicId")
    fun get(musicId: String): Music?
}