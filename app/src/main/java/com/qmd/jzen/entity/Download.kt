package com.qmd.jzen.entity

import com.qmd.jzen.utils.QualityConverter

class Download {
    private var converter: QualityConverter? = null

    constructor(musicId: String?, title: String?, singer: String?, quality: MusicQuality?) {
        this.musicId = musicId
        this.title = title
        this.singer = singer
        this.quality = quality
        converter = QualityConverter(quality)
    }

    constructor(title: String?, singer: String?, quality: MusicQuality?, size: Long) {
        this.title = title
        this.singer = singer
        this.quality = quality
        this.size = size
        if (quality != null) {
            converter = QualityConverter(quality)
        }
    }

    val qualityText: String
        get() = if (quality == null) "" else converter!!.qualityStr

    var musicId: String? = null
    var title: String? = null
    var singer: String? = null
    var quality: MusicQuality? = null
    var size: Long = 0

    var format: String? = null
        get() = if (field == null) {
            converter!!.format
        } else field

    var info: String? = null
        get() = if (field == null) {
            converter!!.info
        } else field

}