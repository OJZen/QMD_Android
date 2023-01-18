package com.qmd.jzen.api.response

/**
 * Create by OJun on 2021/12/27.
 *
 */

/**
 * @property type 0 :id 1:type 2:title 3:content
 */
data class Notification(val id: Int, val type: Int, val target: String, val title: String, val content: String)
