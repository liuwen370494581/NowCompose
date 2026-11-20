package com.lw.nowcompose.data.model

// 页面模型使用真实文本，不再持有 Android 字符串资源 ID，也不依赖 Retrofit 注解。
data class Article(
    val id: String,
    val title: String,
    val summary: String,
    val type: String,
    val publishDate: String,
)
