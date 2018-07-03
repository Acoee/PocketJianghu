package com.app.jianghu.manager

import com.app.jianghu.common.network.RemoteRequest
import com.app.jianghu.common.threads.WorkThreadExecutor
import com.app.jianghu.entity.BannerPO
import com.app.jianghu.entity.CommResponse
import com.app.jianghu.manager.base.AbstractDataManager
import com.app.jianghu.manager.base.DataManagerCallback
import com.google.gson.reflect.TypeToken

/**
 * 首页网络请求接口管理类
 * Created by Acoe on 2018/1/24.
 */
class HomeDataMgr constructor(callback: DataManagerCallback) : AbstractDataManager(callback) {

    private val listener = DataManagerListener()

    fun bannerQuery(url: String, params: HashMap<String, Any>?, requestCode: Int) {
        WorkThreadExecutor.getInstance().excute(Runnable{
            val resultJson: String? = RemoteRequest.callRemoteApi(url, params, null)
//            val result = Gson().fromJson(resultJson, CommResponse::class.java)
//            val resultMap = Gson().fromJson(result, HashMap::class.java)
            val result = parseResultJson(resultJson, object: TypeToken<CommResponse<Any, Any, BannerPO>>(){})
            listener.sendMessage(result.statusCode, requestCode, result, result.message!!)
        })
    }
}
