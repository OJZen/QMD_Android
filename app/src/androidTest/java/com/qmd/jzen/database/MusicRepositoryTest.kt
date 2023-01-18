package com.qmd.jzen.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.qmd.jzen.database.dao.MusicDao
import com.qmd.jzen.database.entity.Music
import com.qmd.jzen.database.repository.MusicRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Create by OJun on 2021/3/6.
 */
class MusicRepositoryTest {

    private lateinit var musicDao: MusicDao
    private lateinit var db: QMDRoomDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, QMDRoomDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        musicDao = db.musicDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun getAllMusic() {
    }

    @Test
    @Throws(Exception::class)
    fun insert() = runBlocking {
        val music = Music(
            "001", "aaa", "Red", arrayListOf("ST"), "Red", "qqq", true, "2021",
            0, 0, 0, 0, 0,
        )
        musicDao.insert(music)
        val allWords = musicDao.getAll().first()
        assertEquals(allWords[0].musicId, music.musicId)
    }

    @Test
    fun delete() {
    }

    @Test
    fun existMusic() {
        val repository = MusicRepository(musicDao)
        repository.existMusic("001")
    }
}