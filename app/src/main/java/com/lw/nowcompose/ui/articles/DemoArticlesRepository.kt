package com.lw.nowcompose.ui.articles

import java.io.IOException
import kotlinx.coroutines.delay

/** 本地学习数据源。延迟和失败开关仅用于体验加载、错误与重试，不请求网络。 */
class DemoArticlesRepository {
    suspend fun loadArticles(simulateFailure: Boolean = false): List<Article> {
        // delay 挂起协程，不会阻塞主线程，也能响应 ViewModel 清理或重新加载时的取消。
        delay(700)
        if (simulateFailure) throw IOException("Simulated article loading failure")
        return sampleArticles
    }
}
