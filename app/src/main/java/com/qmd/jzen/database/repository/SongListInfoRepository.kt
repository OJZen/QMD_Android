package com.qmd.jzen.database.repository

import com.qmd.jzen.database.dao.SongListInfoDao
import com.qmd.jzen.database.entity.SongListInfo

/**
 * Create by OJun on 2022/3/5.
 *
 */
class SongListInfoRepository(private val dao: SongListInfoDao) {
    val allSongList = dao.getAll()

    fun delete(vararg item: SongListInfo) {
        dao.delete(*item)
    }

    fun delete(list: ArrayList<SongListInfo>) {
        dao.delete(*(list.toTypedArray()))
    }

    fun insert(vararg list: SongListInfo) {
        dao.insert(*list)
    }

    fun deleteAll() {
        dao.deleteAll()
    }
}