package com.lw.nowcompose.data.network

import kotlinx.serialization.Serializable

// DTO 与接口字段一致；无需展示的图片、作者和话题 ID 由 ignoreUnknownKeys 忽略。
@Serializable
data class NetworkArticle(
    val id: String,
    val title: String,
    val content: String,
    val type: String,
    val publishDate: String,
)
