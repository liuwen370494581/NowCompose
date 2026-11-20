package com.lw.nowcompose.ui.articles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lw.nowcompose.NowComposeApplication
import com.lw.nowcompose.data.repository.ArticlesRepository
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

class ArticlesViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ArticlesRepository,
) : ViewModel() {
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

    private fun reload() {
        // 新加载取代旧加载，快速重复操作时不会被旧结果覆盖。
        loadJob?.cancel()
        loadState.value = ArticlesFeedState.Loading
        loadJob = viewModelScope.launch {
            try {
                val articles = repository.loadArticles()
                // 完整列表加载成功后，只保留仍然存在的文章 ID，避免旧演示收藏影响计数。
                val availableIds = articles.map { it.id }.toSet()
                savedStateHandle["bookmarked_ids"] = ArrayList(
                    bookmarkedIds.value.filter { it in availableIds },
                )
                loadState.value = ArticlesFeedState.Content(articles)
            } catch (_: IOException) {
                loadState.value = ArticlesFeedState.Error(ArticleLoadError.NETWORK)
            } catch (_: HttpException) {
                loadState.value = ArticlesFeedState.Error(ArticleLoadError.HTTP)
            } catch (_: SerializationException) {
                loadState.value = ArticlesFeedState.Error(ArticleLoadError.INVALID_DATA)
            }
            // 只处理预期的加载失败；CancellationException 会自然向上传播。
        }
    }

    companion object {
        // 框架创建 SavedStateHandle；Application 容器提供共享的 Repository。
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as NowComposeApplication
                ArticlesViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    repository = application.container.articlesRepository,
                )
            }
        }
    }
}
