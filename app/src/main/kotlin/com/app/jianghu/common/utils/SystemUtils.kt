package com.app.jianghu.common.utils

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import cn.com.acoe.jianghu.app.R
import com.app.jianghu.app.AppContext
import com.app.jianghu.common.RandomUtil
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import java.util.jar.Manifest

/**
 * 系统工具类
 * Created by Acoe on 2018/1/22.
 */
class SystemUtils {
    companion object {
        /**
         * 判断给定字符串是否空白串。
         * 空白串是指由空格、制表符、回车符、换行符组成的字符串
         * 若输入字符串为null或空字符串，返回true
         */
        fun isEmpty(input: String?): Boolean {
            if (input == null || "".equals(input)) {
                return true
            }
            for (i in input.indices) {
                val c = input[i].toString()
                if (c != " " && c != "\t" && c!= "\r" && c != "\n") {
                    return false
                }
            }
            return true
        }

        /**
         * 获取手机的DeviceId作为IMEI号
         */
        fun getIMEI(context: AppContext): String {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return telephonyManager.getDeviceId()
            }
            return ""
        }

        lateinit var toast: Toast
        /**
         * 自定义Toast显示
         */
        fun toastShow(msg: String, isLongShow: Boolean) {
            if (toast == null) {
                toast = Toast.makeText(AppContext.appContext, msg, Toast.LENGTH_LONG);
                val toastView = LayoutInflater.from(AppContext.appContext).inflate(R.layout.toast_view, null)
                toast.view = toastView
            }
            if (isLongShow) {
                toast.duration = Toast.LENGTH_LONG
            } else {
                toast.duration = Toast.LENGTH_SHORT
            }
            val toastText = toast.view as TextView
            toastText.text = msg
            toast.show()
        }

        /**
         * 生成文件名(yyyyMMddHHmmss+4位随机字母数字)
         */
        fun dateFileName(): String {
            val c: Calendar = Calendar.getInstance()
            val df = SimpleDateFormat("yyyyMMddHHmmss")
            val date = c.time
            val sysTime = df.format(date)
            return sysTime + RandomUtil.getRandomStringByLength(4)
        }
    }
}