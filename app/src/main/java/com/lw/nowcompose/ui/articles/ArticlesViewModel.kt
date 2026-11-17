package com.lw.nowcompose.ui.articles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticlesViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val repository = DemoArticlesRepository()
    private val loadState = MutableStateFlow<ArticlesFeedState>(ArticlesFeedState.Loading)
    private var loadJob: Job? = null

    // SavedStateHandle 保存少量用户状态；文章数据在 ViewModel 重建后重新加载。
    private val bookmarkedIds = savedStateHandle.getStateFlow(
        "bookmarked_ids", arrayListOf<String>(),
    )
    private val bookmarksOnly = savedStateHandle.getStateFlow("bookmarks_only", false)

    // 三个输入统一生成一个只读 StateFlow，收藏、筛选和列表不会各自维护重复状态。
    val uiState: StateFlow<ArticlesUiState> = combine(
        loadState,
        bookmarkedIds,
        bookmarksOnly,
    ) { loaded, savedIds, onlySaved ->
        val allArticles = (loaded as? ArticlesFeedState.Content)?.articles.orEmpty()
        val visibleArticles = if (onlySaved) {
            allArticles.filter { it.id in savedIds }
        } else {
            allArticles
        }
        ArticlesUiState(
            feed = when (loaded) {
                is ArticlesFeedState.Content -> if (visibleArticles.isEmpty()) {
                    ArticlesFeedState.Empty
                } else {
                    ArticlesFeedState.Content(visibleArticles)
                }
                else -> loaded
            },
            totalCount = allArticles.size,
            bookmarkedIds = savedIds.toList(),
            bookmarksOnly = onlySaved,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ArticlesUiState(
            bookmarkedIds = bookmarkedIds.value.toList(),
            bookmarksOnly = bookmarksOnly.value,
        ),
    )

    init {
        reload()
    }

    fun setBookmarksOnly(value: Boolean) {
        savedStateHandle["bookmarks_only"] = value
    }

    fun toggleBookmark(articleId: String) {
        savedStateHandle["bookmarked_ids"] = ArrayList(bookmarkedIds.value).apply {
            if (articleId in this) remove(articleId) else add(articleId)
        }
    }

    fun retry() = reload()

    fun simulateLoadFailure() = reload(simulateFailure = true)

    private fun reload(simulateFailure: Boolean = false) {
        // 新加载取代旧加载，快速重复操作时不会被旧结果覆盖。
        loadJob?.cancel()
        loadState.value = ArticlesFeedState.Loading
        loadJob = viewModelScope.launch {
            try {
                loadState.value = ArticlesFeedState.Content(repository.loadArticles(simulateFailure))
            } catch (_: IOException) {
                loadState.value = ArticlesFeedState.Error
            }
            // 只处理预期的加载失败；CancellationException 会自然向上传播。
        }
    }
}
