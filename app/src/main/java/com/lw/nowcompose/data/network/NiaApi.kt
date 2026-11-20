package com.lw.nowcompose.data.network

import retrofit2.http.GET

interface NiaApi {
    // 文件的顶层是 JSON 数组，没有 { "data": ... } 包装，不能照搬 prod 接口的响应类。
    @GET("news.json")
    suspend fun getArticles(): List<NetworkArticle>
}
