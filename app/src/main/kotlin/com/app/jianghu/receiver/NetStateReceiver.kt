package com.app.jianghu.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.jianghu.app.AppConfig
import com.app.jianghu.common.utils.ClientInfo

/**
 * 网络状态改变监听
 */
class NetStateReceiver: BroadcastReceiver() {
    companion object {
        var mListeners = ArrayList<NetEventHandler>()
        private val NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(AppConfig.TAG, "=========NetStateReceiver=========")
        if (NET_CHANGE_ACTION == intent?.action) {
            // 修改网络状态值并返回
            ClientInfo.getInstance().getNetworkState()
            // 通知接口完成加载
            if (mListeners.size > 0) {
                for (handler in mListeners) {
                    handler.onNetChange()
                }
            }
        }
    }

    interface NetEventHandler {
        fun onNetChange()
    }
}