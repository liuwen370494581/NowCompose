package com.lw.nowcompose.ui.articles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lw.nowcompose.R
import com.lw.nowcompose.ui.theme.NowComposeTheme

// 页面不持有业务状态，也不读取 sampleArticles；只根据参数展示内容并上报事件。
@Composable
fun ArticlesScreen(
    articles: List<Article>,
    totalCount: Int,
    bookmarkedIds: List<String>,
    bookmarksOnly: Boolean,
    onBookmarksOnlyChange: (Boolean) -> Unit,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                        selected = !bookmarksOnly,
                        onClick = { onBookmarksOnlyChange(false) },
                        label = { Text(stringResource(R.string.all_articles, totalCount)) },
                    )
                    FilterChip(
                        selected = bookmarksOnly,
                        onClick = { onBookmarksOnlyChange(true) },
                        label = { Text(stringResource(R.string.saved_articles, bookmarkedIds.size)) },
                    )
                }
            }
        }
        if (articles.isEmpty()) {
            item(key = "empty") {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.empty_bookmarks_title),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = stringResource(R.string.empty_bookmarks_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        // 用文章 ID 作为 key，收藏筛选导致条目增删时仍能正确识别每一项。
        items(articles, key = { it.id }) { article ->
            ArticleCard(
                article = article,
                isBookmarked = article.id in bookmarkedIds,
                onToggleBookmark = { onToggleBookmark(article.id) },
            )
        }
    }
}

@Preview(name = "全部文章", showBackground = true)
@Preview(
    name = "全部文章 · 深色",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ArticlesScreenPreview() {
    NowComposeTheme(dynamicColor = false) {
        ArticlesScreen(
            articles = sampleArticles,
            totalCount = sampleArticles.size,
            bookmarkedIds = listOf(sampleArticles.first().id),
            bookmarksOnly = false,
            onBookmarksOnlyChange = {},
            onToggleBookmark = {},
        )
    }
}

@Preview(name = "只看收藏", showBackground = true)
@Composable
private fun BookmarkedArticlesPreview() {
    NowComposeTheme(dynamicColor = false) {
        ArticlesScreen(
            articles = sampleArticles.take(2),
            totalCount = sampleArticles.size,
            bookmarkedIds = sampleArticles.take(2).map { it.id },
            bookmarksOnly = true,
            onBookmarksOnlyChange = {},
            onToggleBookmark = {},
        )
    }
}

@Preview(name = "收藏为空", showBackground = true)
@Composable
private fun EmptyBookmarksPreview() {
    NowComposeTheme(dynamicColor = false) {
        ArticlesScreen(
            articles = emptyList(),
            totalCount = sampleArticles.size,
            bookmarkedIds = emptyList(),
            bookmarksOnly = true,
            onBookmarksOnlyChange = {},
            onToggleBookmark = {},
        )
    }
}
