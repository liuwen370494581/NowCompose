package com.lw.nowcompose.data

import com.lw.nowcompose.data.network.NiaApi
import com.lw.nowcompose.data.repository.ArticlesRepository
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/** 手动组装依赖：学习阶段先看清 Api → Repository → ViewModel 的关系，无需 Hilt。 */
class AppContainer {
    private val json = Json { ignoreUnknownKeys = true }

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val api: NiaApi by lazy {
        Retrofit.Builder()
            // NIA 的 prod 后端不公开；这里通过 HTTPS 读取官方仓库的静态 JSON。
            .baseUrl("https://raw.githubusercontent.com/android/nowinandroid/main/core/network/src/main/assets/")
            // 首次请求时才创建 OkHttp，避免在主线程组装 ViewModel 时初始化客户端。
            .callFactory { request -> client.newCall(request) }
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(NiaApi::class.java)
    }

    val articlesRepository: ArticlesRepository by lazy { ArticlesRepository(api) }
}
