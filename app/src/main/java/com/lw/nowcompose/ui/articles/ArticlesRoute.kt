package com.lw.nowcompose.ui.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 文章页的状态入口。这里的 Route 只是命名约定，尚未引入导航库。
 *
 * ViewModel 管理状态，Route 负责收集并连接事件，Screen 负责显示。
 */
@Composable
fun ArticlesRoute(
    modifier: Modifier = Modifier,
    viewModel: ArticlesViewModel = viewModel(),
) {
    // 界面进入后台时停止收集，回到前台时接收最新状态。
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArticlesScreen(
        uiState = uiState,
        onBookmarksOnlyChange = viewModel::setBookmarksOnly,
        onToggleBookmark = viewModel::toggleBookmark,
        onRetry = viewModel::retry,
        onSimulateFailure = viewModel::simulateLoadFailure,
        modifier = modifier,
    )
}
