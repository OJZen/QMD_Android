package com.qmd.jzen.api.request

import com.qmd.jzen.api.entities.Comm

/**
 * Create by OJun on 2021/9/25.
 *
 */
data class MusicUrlReqBody(val comm: Comm, val queryVKey: MusicUrlModule)

data class MusicUrlModule(val method: String, val module: String, val param: MusicUrlParam)

data class MusicUrlParam(
    val ctx: Int, val downloadFrom: Int, val fileName: List<String>,
    val guid: String, val referer: String, val scene: Int,
    val songMid: List<String>, val songType: List<Int>, val uin: String
)

/*
{
    "comm": {
        "ct": "19",
        "cv": "1777"
    },
    "queryvkey": {
        "method": "CgiGetVkey",
        "module": "vkey.GetVkeyServer",
        "param": {
            "ctx": 1,
            "downloadfrom": 0,
            "filename": [
                "F000001YxfVL2UdnwB.flac"
            ],
            "guid": "aa",
            "referer": "y.qq.com",
            "scene": 0,
            "songmid": [
                "000ruq2M3hZIAO"
            ],
            "songtype": [
                1
            ],
            "uin": "00"
        }
    }
 */