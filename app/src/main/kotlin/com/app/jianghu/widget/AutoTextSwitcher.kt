package com.app.jianghu.widget

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import android.widget.TextView
import cn.com.acoe.jianghu.app.R
import java.util.*
import kotlin.collections.ArrayList

/**
 * 垂直单行跑马灯控件
 */
class AutoTextSwitcher<T> : TextSwitcher {
    constructor(context: Context):super(context)
    constructor(context: Context, attrs: AttributeSet):super(context, attrs)
    init {
        setFactory{ getView(context) }
        inAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_from_bottom)
        outAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_top)
    }

    private var timer: Timer? = null // 定时器
    private var list: ArrayList<T>? = null // 列表数据
    private var array: Array<String>? = null // 数组数据
    private var time = 2000L // 滚动间隔
    private var className: String? = null
    private var propertyName: String? = null
    private var index = 0 // 当前显示的数据索引
    private var runFlag = 1 // 滚动标识, 1 滚动, 2 停止
    private var mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                1 -> next(className!!, propertyName!!)
                2 -> next()
            }
        }
    }

    private fun getView(context: Context): TextView {
        var tv = TextView(context)
        if (Build.VERSION.SDK_INT < 23) {
            tv.setTextAppearance(context, R.style.normal_text_style)
        } else {
            tv.setTextAppearance(R.style.normal_text_style)
        }
        return tv
    }

    /**
     * 跑马灯初始化，并启动
     */
    fun start(list: ArrayList<T>, time: Long?, className: String, propertyName: String) {
        runFlag = 1
        this.list = list
        this.className = className
        this.propertyName = propertyName
        if (time != null) {
            this.time = time
        }
        if (timer == null) {
            timer = Timer()
        }
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                mHandler.sendEmptyMessage(runFlag)
            }
        }, 1, this@AutoTextSwitcher.time)
    }

    /**
     * 跑马灯初始化，并启动
     */
    fun start(array: Array<String>, time: Long?) {
        runFlag = 2
        this.array = array
        if (time != null) {
            this.time = time
        }
        if (timer == null) {
            timer = Timer()
        }
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                mHandler.sendEmptyMessage(runFlag)
            }
        }, 1, this@AutoTextSwitcher.time)
    }

    /**
     * 跑马灯启动
     */
    fun start() {
        if (list != null) {
            runFlag = 1
        } else if (array != null) {
            runFlag = 2
        }
    }

    /**
     * 停止
     */
    fun stop() {
        runFlag = 2
    }

    private fun next(className: String, propertyName: String) {
        setText(getValue(list!![index], className, propertyName))
        if (++index >= array!!.size) {
            index = 0
        }
    }

    private fun next() {
        setText(array!![index])
        if (++index >= array!!.size) {
            index = 0
        }
    }

    private fun getValue(obj: T, className: String, propertyName: String): String {
        val any = Class.forName(className).newInstance()
        val field = any.javaClass.getDeclaredField(propertyName)
        return field.get(any).toString()
    }

}