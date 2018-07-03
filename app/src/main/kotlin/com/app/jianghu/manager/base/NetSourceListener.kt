package com.app.jianghu.manager.base

/**
 * Created by Acoe on 2018/1/24.
 */
interface NetSourceListener {
    companion object {
        /**以下为服务端定义的code*/
        val WHAT_SUCCESS = 200 // 成功
        val WHAT_ERROR = 0 // 失败
        val WHAT_NOT_LOGIN = 300000007 // 未登录

        val IS_CACHE_LOAD = 10 // 缓存加载成功
        val ISNOT_CACHE_LOAD = 11 // 不是缓存加载成功

        val REQUEST_FAILURE_MSG = "请求响应失败 , 请重试！" // 服务端请求失败
    }
    fun sendMessage(what: Int, requestCode: Int, data: Any?, toast: String)
    fun sendMessage(what: Int, requestCode: Int, cacheCode: Int, data: Any?, toast: String)
}