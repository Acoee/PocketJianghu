package com.app.jianghu.app

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*

/**
 * Created by Acoe on 2018/1/30.
 */
class AppManager private constructor(){
    companion object {
        private var activityStack: Stack<Activity>? = null // 保存Activity的栈
        private var instance: AppManager? = null // 当前类的实例

        /**
         * 返回当前类的实例
         */
        fun getAppManager(): AppManager? {
            if (instance == null) {
                instance = AppManager()
            }
            return instance
        }
    }

    /**
     * 添加Activty到堆栈
     */
    fun addActivity(activty: Activity) {
        if (activityStack == null) {
            activityStack = Stack<Activity>()
        }
        activityStack!!.add(activty)
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity {
        return activityStack!!.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        val activity: Activity = activityStack!!.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity) {
        if (activity != null) {
            activityStack?.remove(activity)
            activity.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        var removes: Stack<Activity> = Stack<Activity>()
        for (activity in activityStack!!) {
            if (activity.javaClass == cls) {
                removes.add(activity)
                activity.finish()
            }
        }
        activityStack!!.removeAll(removes)
    }

    /**
     * 返回指定类名的Activity
     */
    fun getActivity(cls: Class<*>): Activity? {
        for (activity in activityStack!!) {
            if (activity.javaClass == cls) {
                return activity
            }
        }
        return null
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        for (activty in activityStack!!) {
            activty.finish()
        }
        activityStack!!.clear()
    }

    /**
     * 退出应用程序
     */
    fun AppExit() {
        finishAllActivity()
        val activityMgr: ActivityManager = AppContext.appContext?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityMgr.killBackgroundProcesses(AppContext.appContext?.packageName)
        System.exit(0)
    }
}