package com.lw.nowcompose.ui.articles

import androidx.annotation.StringRes
import com.lw.nowcompose.R

// 第一版使用本地示例文案，所以保存资源 ID。接入真实数据时再换成数据层模型。
data class Article(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val summaryRes: Int,
    @StringRes val topicRes: Int,
    val readingMinutes: Int,
)

val sampleArticles = listOf(
    Article(
        id = "compose-ui",
        titleRes = R.string.article_ui_title,
        summaryRes = R.string.article_ui_summary,
        topicRes = R.string.topic_basics,
        readingMinutes = 3,
    ),
    Article(
        id = "compose-layout",
        titleRes = R.string.article_layout_title,
        summaryRes = R.string.article_layout_summary,
        topicRes = R.string.topic_layout,
        readingMinutes = 5,
    ),
    Article(
        id = "compose-state",
        titleRes = R.string.article_state_title,
        summaryRes = R.string.article_state_summary,
        topicRes = R.string.topic_state,
        readingMinutes = 6,
    ),
    Article(
        id = "compose-list",
        titleRes = R.string.article_list_title,
        summaryRes = R.string.article_list_summary,
        topicRes = R.string.topic_lists,
        readingMinutes = 4,
    ),
    Article(
        id = "compose-components",
        titleRes = R.string.article_components_title,
        summaryRes = R.string.article_components_summary,
        topicRes = R.string.topic_components,
        readingMinutes = 5,
    ),
)
