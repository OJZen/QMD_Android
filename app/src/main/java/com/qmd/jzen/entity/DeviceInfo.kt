package com.qmd.jzen.entity

/**
 * Create by OJun on 2021/2/24.
 */
data class DeviceInfo(
    val uid: String,
    val deviceModel: String,
    val deviceBrand: String,
    val systemVersion: String,
    val appVersion: String,
    val versionCode: String,
    var ip: String? = null
)