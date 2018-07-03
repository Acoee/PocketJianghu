package com.app.jianghu.common.threads

import android.os.Process
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 网络请求线程池
 * Created by Acoe on 2018/1/19.
 */
class WorkThreadExecutor private constructor(){
    private var executor: ExecutorService? = null

    companion object {
        private var mInstance: WorkThreadExecutor? = null

        @JvmStatic
        fun getInstance(): WorkThreadExecutor {
            if (mInstance == null) {
                mInstance = WorkThreadExecutor()
                val threadFactory = PriorityThreadFactory("work-thread", Process.THREAD_PRIORITY_BACKGROUND)
                mInstance?.executor = Executors.newFixedThreadPool(5, threadFactory)
            }
            return mInstance!!
        }
    }

    fun excute(task: Runnable) {
        mInstance?.executor?.execute(task)
    }
}