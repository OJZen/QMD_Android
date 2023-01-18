package com.qmd.jzen.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.qmd.jzen.database.dao.MusicDao
import com.qmd.jzen.database.dao.SearchHistoryDao
import com.qmd.jzen.database.dao.SongListInfoDao
import com.qmd.jzen.database.entity.Music
import com.qmd.jzen.database.entity.SearchHistory
import com.qmd.jzen.database.entity.SongListInfo

/**
 * Create by OJun on 2021/3/5.
 *
 */
@Database(entities = [Music::class, SearchHistory::class, SongListInfo::class], version = 4, exportSchema = false)
abstract class QMDRoomDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDao

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun songListInfoDao(): SongListInfoDao

    companion object {
        // 只增加了一个表
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `SongList` (`sid` INTEGER NOT NULL, `title` TEXT NOT NULL, `desc` TEXT NOT NULL, `logoUrl` TEXT NOT NULL, `creatorName` TEXT NOT NULL, `num` INTEGER NOT NULL, `addTime` INTEGER NOT NULL, PRIMARY KEY(`sid`))")
            }
        }

        private var INSTANCE: QMDRoomDatabase? = null
        fun getDatabase(context: Context): QMDRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QMDRoomDatabase::class.java,
                    "qmd_datebase"
                ).addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }


    }
}