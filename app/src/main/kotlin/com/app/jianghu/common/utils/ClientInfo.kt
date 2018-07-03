package com.app.jianghu.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.util.Log
import com.app.jianghu.app.AppConfig
import com.app.jianghu.app.AppContext

/**设备信息工具类
 * Created by Acoe on 2018/1/19.
 */
class ClientInfo {
    var mNetworkState: Int = NETWORK_NONE // 当前网络状态
    var userAgent: String? = null // 当前手机客户端信息
    var versionName: String? = null // 系统版本名
    var versionCode: Int = 0 // 系统版本号
    lateinit var androidId: String // AndroidID（android_id + imei）
    lateinit var terminalCode: String // 终端码

    companion object {
        private var mInstance: ClientInfo?= null;
        val NETWORK_NONE: Int = 0 // 无网络
        val NETWORK_WIFI: Int = 1 // wifi网络
        val NETWORK_MOBILE: Int = 2 // 基站网络

        @JvmStatic
        fun getInstance(): ClientInfo {
            if (mInstance == null) {
                mInstance = ClientInfo()
                mInstance?.init()
            }
            return mInstance!!
        }
    }

    fun init() {
        val context = AppContext.appContext
        androidId = "Android_" + Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID + "_" + SystemUtils.getIMEI(context!!))
        setVersionAndName(context)
        setUserAgent(context)
        getNetworkState()
    }

    /**
     * 获取APP安装包信息
     */
    private fun setVersionAndName(context: Context) {
        var info  = context.packageManager.getPackageInfo(context.packageName, 0)
        versionName = info.versionName
        versionCode = info.versionCode
    }

    /**
     * 获取手机的标识信息和应用的版本信息
     */
    private fun setUserAgent(context: Context) {
        var ua = StringBuilder("JH.APP")
        ua.append("/$versionName _$versionCode") // APP版本
        ua.append("/Android") // 手机系统平台
        ua.append("/" + android.os.Build.VERSION.RELEASE) // 手机系统版本
        ua.append("/" + android.os.Build.MODEL) // 手机型号
        ua.append("/" + androidId) // 客户端唯一标识
        userAgent = ua.toString()
        Log.i(AppConfig.TAG, "userAgent---------------->" + userAgent)
    }

    /**
     * 判断当前网络状态
     * @return 0：无网络 1：WIFI网络 2：蜂窝网络
     */
    fun getNetworkState(): Int {
        val connManager: ConnectivityManager = AppContext.appContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetWork: NetworkInfo? = connManager.activeNetworkInfo
        if (activeNetWork != null && activeNetWork.isConnected) {
            if (activeNetWork.type == ConnectivityManager.TYPE_WIFI) {
                mNetworkState = NETWORK_WIFI
            } else if (activeNetWork.type == ConnectivityManager.TYPE_MOBILE) {
                mNetworkState = NETWORK_MOBILE
            }
        }
        Log.i(AppConfig.TAG, "network status---------------->" + mNetworkState)
        return mNetworkState
    }
}
