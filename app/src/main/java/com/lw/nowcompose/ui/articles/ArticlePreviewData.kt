package com.lw.nowcompose.ui.articles

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lw.nowcompose.R
import com.lw.nowcompose.data.model.Article

// 只供 Preview 使用；运行页面始终请求网络，失败时不会用这份假数据替代。
@Composable
internal fun previewArticles(): List<Article> = listOf(
    Article(
        id = "preview-ui",
        title = stringResource(R.string.article_ui_title),
        summary = stringResource(R.string.article_ui_summary),
        type = stringResource(R.string.topic_basics),
        publishDate = "2026-11-17",
    ),
    Article(
        id = "preview-state",
        title = stringResource(R.string.article_state_title),
        summary = stringResource(R.string.article_state_summary),
        type = stringResource(R.string.topic_state),
        publishDate = "2026-11-17",
    ),
)
