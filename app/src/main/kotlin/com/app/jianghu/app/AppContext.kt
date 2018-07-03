package com.app.jianghu.app

import android.app.Application
import android.os.Handler
import com.app.jianghu.common.cache.ImageMemoryCache
import com.app.jianghu.common.network.Urls
import com.app.jianghu.common.utils.ClientInfo

/**
 * Created by Acoe on 2018/1/19.
 */
class AppContext: Application() {

    companion object {
        var appContext: AppContext? = null
        lateinit var handle: Handler
        var isLogined: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        ClientInfo.getInstance().getNetworkState() // 检查网络状态
        handle = Handler()
        Urls() // 初始化接口地址
    }

    override fun onLowMemory() {
        super.onLowMemory()
        ImageMemoryCache.clearMemoryCache()
    }
}