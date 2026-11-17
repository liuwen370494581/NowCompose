package com.lw.nowcompose.ui.articles

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lw.nowcompose.R
import com.lw.nowcompose.ui.theme.NowComposeTheme

// Screen 只根据 UiState 画界面；收藏、筛选、重试都通过回调交给 ViewModel。
@Composable
fun ArticlesScreen(
    uiState: ArticlesUiState,
    onBookmarksOnlyChange: (Boolean) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onRetry: () -> Unit,
    onSimulateFailure: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val loading = uiState.feed is ArticlesFeedState.Loading
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.articles_title),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = stringResource(R.string.articles_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = !uiState.bookmarksOnly,
                        onClick = { onBookmarksOnlyChange(false) },
                        enabled = !loading,
                        label = { Text(stringResource(R.string.all_articles, uiState.totalCount)) },
                    )
                    FilterChip(
                        selected = uiState.bookmarksOnly,
                        onClick = { onBookmarksOnlyChange(true) },
                        enabled = !loading,
                        label = {
                            Text(stringResource(R.string.saved_articles, uiState.bookmarkedIds.size))
                        },
                    )
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onRetry, enabled = !loading) {
                        Text(stringResource(R.string.reload_articles))
                    }
                    TextButton(onClick = onSimulateFailure, enabled = !loading) {
                        Text(stringResource(R.string.simulate_articles_failure))
                    }
                }
            }
        }
        // sealed interface 的 when 覆盖所有情况，新增状态时编译器会提醒补齐分支。
        when (val feed = uiState.feed) {
            ArticlesFeedState.Loading -> item(key = "loading") {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.articles_loading))
                }
            }
            is ArticlesFeedState.Content -> {
                // 稳定 ID 让收藏筛选导致条目增删时仍能正确识别文章。
                items(feed.articles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        isBookmarked = article.id in uiState.bookmarkedIds,
                        onToggleBookmark = { onToggleBookmark(article.id) },
                    )
                }
            }
            ArticlesFeedState.Empty -> item(key = "empty") {
                ArticleStateCard(
                    titleRes = if (uiState.bookmarksOnly) {
                        R.string.empty_bookmarks_title
                    } else {
                        R.string.empty_articles_title
                    },
                    descriptionRes = if (uiState.bookmarksOnly) {
                        R.string.empty_bookmarks_description
                    } else {
                        R.string.empty_articles_description
                    },
                )
            }
            ArticlesFeedState.Error -> item(key = "error") {
                ArticleStateCard(
                    titleRes = R.string.articles_error_title,
                    descriptionRes = R.string.articles_error_description,
                    action = {
                        Button(onClick = onRetry) {
                            Text(stringResource(R.string.retry_articles))
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ArticleStateCard(
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    modifier: Modifier = Modifier,
    action: @Composable () -> Unit = {},
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(titleRes), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(descriptionRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            action()
        }
    }
}

@Preview(name = "内容", showBackground = true)
@Preview(
    name = "内容 · 深色",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ArticlesContentPreview() {
    ArticlesStatePreview(
        ArticlesUiState(
            feed = ArticlesFeedState.Content(sampleArticles),
            totalCount = sampleArticles.size,
            bookmarkedIds = listOf(sampleArticles.first().id),
        ),
    )
}

@Preview(name = "加载中", showBackground = true)
@Composable
private fun ArticlesLoadingPreview() {
    ArticlesStatePreview(ArticlesUiState())
}

@Preview(name = "收藏为空", showBackground = true)
@Composable
private fun EmptyBookmarksPreview() {
    ArticlesStatePreview(
        ArticlesUiState(
            feed = ArticlesFeedState.Empty,
            totalCount = sampleArticles.size,
            bookmarksOnly = true,
        ),
    )
}

@Preview(name = "加载失败", showBackground = true)
@Composable
private fun ArticlesErrorPreview() {
    ArticlesStatePreview(ArticlesUiState(feed = ArticlesFeedState.Error))
}

@Composable
private fun ArticlesStatePreview(uiState: ArticlesUiState) {
    NowComposeTheme(dynamicColor = false) {
        ArticlesScreen(
            uiState = uiState,
            onBookmarksOnlyChange = {},
            onToggleBookmark = {},
            onRetry = {},
            onSimulateFailure = {},
        )
    }
}
