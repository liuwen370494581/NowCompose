package com.lw.nowcompose.ui.articles

import com.lw.nowcompose.data.model.Article

// 同一时刻只能处于一种列表状态，避免 isLoading、hasError 等多个布尔值互相矛盾。
sealed interface ArticlesFeedState {
    data object Loading : ArticlesFeedState
    data class Content(val articles: List<Article>) : ArticlesFeedState
    data object Empty : ArticlesFeedState
    data class Error(val reason: ArticleLoadError) : ArticlesFeedState
}

enum class ArticleLoadError { NETWORK, HTTP, INVALID_DATA }

// 页面绘制需要的数据集中在这里，UI 不直接修改它。
data class ArticlesUiState(
    val feed: ArticlesFeedState = ArticlesFeedState.Loading,
    val totalCount: Int = 0,
    val bookmarkedIds: List<String> = emptyList(),
    val bookmarksOnly: Boolean = false,
)
