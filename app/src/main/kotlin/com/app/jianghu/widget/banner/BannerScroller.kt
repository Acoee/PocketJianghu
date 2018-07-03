package com.app.jianghu.widget.banner

import android.content.Context
import android.util.Log
import android.view.animation.Interpolator
import android.widget.Scroller

class BannerScroller : Scroller {
    var animTime = 0

    constructor(context: Context, interpolator: Interpolator): super(context, interpolator)

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, duration)
        Log.i("BannerScroller", "startScroll->duration: $duration")
        if (duration % 100 == 0) { // 通过查看源码，ViewPager设置currentItem来滑动一页的时间都是100的整数倍
            super.startScroll(startX, startY, dx, dy, animTime)
        } else {
            super.startScroll(startX, startY, dx, dy, duration)
        }
    }
}