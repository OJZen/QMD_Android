package com.qmd.jzen.api.entities

import com.google.gson.annotations.SerializedName

/**
 * Create by OJun on 2022/1/19.
 *
 */
data class PayInfo(
    @SerializedName("pay_down") val payDown: Int,
    @SerializedName("pay_month") val payMonth: Int,
    @SerializedName("pay_status") val payStatus: Int,
    @SerializedName("price_album") val priceAlbum: Int,
    @SerializedName("price_track") val priceTrack: Int
)