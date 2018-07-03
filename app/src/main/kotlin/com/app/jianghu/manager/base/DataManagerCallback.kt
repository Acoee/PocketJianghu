package com.app.jianghu.manager.base

/**
 * Created by Acoe on 2018/1/24.
 */
interface DataManagerCallback {
    fun onBack(what: Int, requestCode: Int, cacheCode: Int, data: Any?)
}