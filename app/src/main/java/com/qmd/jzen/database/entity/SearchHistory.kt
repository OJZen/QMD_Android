package com.qmd.jzen.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Create by OJun on 2022/1/25.
 * 搜索历史的实体
 */
@Entity
data class SearchHistory(@PrimaryKey val keyword: String, var time: Long)
