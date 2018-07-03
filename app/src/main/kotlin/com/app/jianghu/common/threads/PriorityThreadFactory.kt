package com.app.jianghu.common.threads

import android.os.Process
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * 指定优先级线程工厂类
 * Created by Acoe on 2018/1/19.
 */
class PriorityThreadFactory (private val mName: String, private val mPriority: Int): ThreadFactory{
    private val mNumber = AtomicInteger()

    override fun newThread(r: Runnable?): Thread {
        return object : Thread(r, mName + "-" + mNumber.getAndIncrement()) {
            override fun run() {
                Process.setThreadPriority(mPriority)
                super.run()
            }
        }
    }
}