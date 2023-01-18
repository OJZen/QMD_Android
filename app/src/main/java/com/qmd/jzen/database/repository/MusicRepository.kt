package com.qmd.jzen.database.repository

import com.qmd.jzen.api.ApiSource
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.api.repository.QMDRepository
import com.qmd.jzen.api.request.FavouriteReqBody
import com.qmd.jzen.database.dao.MusicDao
import com.qmd.jzen.database.entity.Music
import com.qmd.jzen.utils.SystemInfoUtil
import kotlinx.coroutines.flow.Flow

/**
 * Create by OJun on 2021/3/5.
 *
 */
class MusicRepository(private val musicDao: MusicDao) {

    val allMusic: Flow<List<Music>> = musicDao.getAll()
    private val QMDApi = QMDRepository(ApiSource.getServerApi())

    suspend fun insert(vararg music: Music) {
        musicDao.insert(*music)
        // TODO("后端提供批量收藏接口")
//        music.forEach {
//            val singerName = StringBuilder().apply {
//                it.singer.forEach { name -> append(name + "_") }
//                removeSuffix("_")
//            }
//        }
        try {
            QMDApi.addFavourite(FavouriteReqBody(SystemInfoUtil.UID, music[0].musicId, music[0].title, music[0].getSingerName()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun insert(qMusicList: ArrayList<MusicInfo>) {
        val musicList: ArrayList<Music> = arrayListOf()
        qMusicList.forEach {
            musicList.add(it.toMusic())
        }
        insert(*(musicList.toTypedArray()))
    }

    fun delete(vararg music: Music) {
        musicDao.delete(*music)
    }

    fun delete(music: ArrayList<MusicInfo>) {
        val musicList: ArrayList<Music> = arrayListOf()
        music.forEach {
            musicList.add(it.toMusic())
        }
        musicDao.delete(*(musicList.toTypedArray()))
    }

    fun existMusic(musicId: String): Boolean {
        return musicDao.get(musicId) != null
    }
}