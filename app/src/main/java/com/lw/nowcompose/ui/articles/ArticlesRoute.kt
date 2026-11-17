package com.lw.nowcompose.ui.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * 文章页的状态入口。这里的 Route 只是命名约定，尚未引入导航库。
 *
 * 收藏按钮、筛选按钮和列表都需要访问收藏状态，所以把状态放到它们共同的上层。
 * Screen 和 Card 接收状态，通过回调向这里报告事件。
 */
@Composable
fun ArticlesRoute(modifier: Modifier = Modifier) {
    // ArrayList<String> 可以保存到 Bundle；旋转屏幕后可以恢复，不等于长期存储。
    var bookmarkedIds by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    var bookmarksOnly by rememberSaveable { mutableStateOf(false) }

    // 筛选结果由原始数据和当前状态计算，不再保存一份可变列表，避免两份数据不同步。
    val visibleArticles = if (bookmarksOnly) {
        sampleArticles.filter { it.id in bookmarkedIds }
    } else {
        sampleArticles
    }

    ArticlesScreen(
        articles = visibleArticles,
        totalCount = sampleArticles.size,
        bookmarkedIds = bookmarkedIds,
        bookmarksOnly = bookmarksOnly,
        onBookmarksOnlyChange = { bookmarksOnly = it },
        onToggleBookmark = { articleId ->
            // 替换整个列表，让 Compose 观察到新值，而不是直接修改普通 ArrayList。
            bookmarkedIds = ArrayList(bookmarkedIds).apply {
                if (articleId in this) remove(articleId) else add(articleId)
            }
        },
        modifier = modifier,
    )
}
