package com.lw.nowcompose.data.repository

import com.lw.nowcompose.data.model.Article
import com.lw.nowcompose.data.network.NiaApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArticlesRepository(
    private val api: NiaApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    // Retrofit 的 suspend 请求本身不会阻塞主线程；客户端首次初始化和数据转换也放在 IO。
    // 异常交给 ViewModel 转成页面状态，Repository 不依赖 Compose 或 UiState。
    suspend fun loadArticles(): List<Article> = withContext(ioDispatcher) {
        api.getArticles().map { item ->
            Article(
                id = item.id,
                title = item.title,
                summary = item.content,
                type = item.type,
                publishDate = item.publishDate.substringBefore('T'),
            )
        }
    }
}
