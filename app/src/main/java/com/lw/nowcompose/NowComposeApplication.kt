package com.lw.nowcompose

import android.app.Application
import com.lw.nowcompose.data.AppContainer

class NowComposeApplication : Application() {
    // Application 只持有一个容器，多个 ViewModel 可以复用同一个网络客户端和 Repository。
    val container by lazy { AppContainer() }
}
