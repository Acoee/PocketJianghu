package com.app.jianghu.manager.base

import android.os.Handler
import android.os.Message
import com.app.jianghu.common.network.RemoteRequest
import com.app.jianghu.common.utils.GsonUtil
import com.app.jianghu.common.utils.SystemUtils
import com.app.jianghu.entity.CommResponse
import com.google.gson.reflect.TypeToken
import java.lang.ref.SoftReference
/**
 * Created by Acoe on 2018/1/24.
 */
abstract class AbstractDataManager constructor(callback: DataManagerCallback): Handler() {
    private var callback = SoftReference<DataManagerCallback>(callback) // 回调使用软引用保存
    companion object {
        val USER_UNLOGIN = 10000 // 用户未登陆

        fun getToastMsg(_resultMsg: String): String {
            var resultMsg = _resultMsg
            if (SystemUtils.isEmpty(resultMsg)) {
                resultMsg = RemoteRequest.REQUEST_FAILURE_MESSAGE
            }
            return resultMsg
        }

        /**
         * 处理封装结果
         */
        private fun <K, E, T> getResult(res: CommResponse<K, E, T>?): CommResponse<K, E, T> {
            var result = res
            if (result == null) {
                result =  CommResponse()
                result.statusCode = NetSourceListener.WHAT_ERROR
                result.message = NetSourceListener.REQUEST_FAILURE_MSG
            }
            return result
        }

        /**
         * 解析返回的结果
         */
        fun <K, E, T> parseResultJson(resultJson: String?, typeToken: TypeToken<CommResponse<K, E, T>>): CommResponse<K, E, T> {
            var result: CommResponse<K, E, T>? = null
            if (!SystemUtils.isEmpty(resultJson)) {
                val type = typeToken.type
                result = GsonUtil.getObjectFromJson(resultJson!!, type)
            }
            return getResult(result)
        }
    }

    protected inner class DataManagerListener: NetSourceListener {

        override fun sendMessage(what: Int, requestCode: Int, data: Any?, toast: String) {
            sendMessage(what, requestCode, NetSourceListener.ISNOT_CACHE_LOAD, data, toast)
        }

        override fun sendMessage(what: Int, requestCode: Int, cacheCode: Int, data: Any?, toast: String) {
            var msg: Message? = null
            when(what) {
                NetSourceListener.WHAT_NOT_LOGIN -> msg = onSessionOutOfDate(what)
                NetSourceListener.WHAT_SUCCESS -> msg = onSuccess(what, data)
                NetSourceListener.WHAT_ERROR -> msg = onError(what, toast)
            }
            if (msg != null) {
                msg.arg1 = requestCode
                msg.arg2 = cacheCode
                // 防止请求响应太快，对话框无法消失
                this@AbstractDataManager.sendMessageDelayed(msg,100)
            }
        }

        private fun onSessionOutOfDate(what: Int): Message {
            return Message.obtain(this@AbstractDataManager, what)
        }

        private fun onSuccess(what: Int, data: Any?): Message {
            return Message.obtain(this@AbstractDataManager, what, data)
        }
        private fun onError(what: Int, toast: String): Message {
            return Message.obtain(this@AbstractDataManager, what, toast)
        }

    }

    override fun handleMessage(msg: Message) {
        handleMessageOnUiThread(msg.what, msg.arg1, msg.arg2, msg.obj)
        val callback: DataManagerCallback? = getCallback()
        callback?.onBack(msg.what, msg.arg1, msg.arg2, msg.obj)
    }

    private fun handleMessageOnUiThread(what: Int, loginCode: Int, cacheCode: Int, data: Any) {}

    private fun getCallback(): DataManagerCallback?{
        return  this.callback?.get()
    }
}

