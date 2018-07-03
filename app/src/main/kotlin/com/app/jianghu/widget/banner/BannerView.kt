package com.app.jianghu.widget.banner

import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import cn.com.acoe.jianghu.app.R
import com.app.jianghu.common.BitmapManager
import kotlinx.android.synthetic.main.banner_layout.view.*
import java.util.*

/**
 * 图片轮播控件
 * Created by Acoe on 2018/2/1.
 */
class BannerView : FrameLayout, ViewPager.OnPageChangeListener {
    private val TAG = "BannerView"
    private var duration: Long = 3500L // 图片切换间隔时间
    private var size = 0 // 轮播图数量

    constructor(context: Context): super(context) {
        initUI()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initUI()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initUI()
    }


    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    /**
     * Viewpager页面切换事件
     */
    override fun onPageSelected(position: Int) {
        if (size < 2) return
        for (i in 0 until dotContainer.childCount) {
            if (i == position % size) {
                dotContainer.getChildAt(i).setBackgroundResource(R.drawable.dot_normal_icon)
            } else {
                dotContainer.getChildAt(i).setBackgroundResource(R.drawable.dot_focus_icon)
            }
        }
    }

    private fun initUI() {
        val view = LayoutInflater.from(context).inflate(R.layout.banner_layout, null)
        addView(view)
        viewPager?.addOnPageChangeListener(this)
    }

    /**
     * 定制轮播滑动速度
     */
    fun start(context: Context, imgUrls: ArrayList<String>, duration: Long?, animTime: Int) {
        try {
            val field = ViewPager::class.java.getDeclaredField("mScroller")
            field.isAccessible = true
            val mScroller = BannerScroller(context, AccelerateInterpolator())
            field.set(viewPager, mScroller)
            mScroller.animTime = animTime
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
        start(context, imgUrls, duration)
    }

    /**
     * 开始轮播
     */
    fun start(context: Context, imgUrls: ArrayList<String>, duration: Long?) {
        if (duration != null) {
            this.duration = duration
        }
        dotContainer.removeAllViews()
        size = imgUrls.size
        if (imgUrls.size >= 2) {
            // 圆点大小是圆点容器高度的80%
            val dotHeight = (dotContainer.layoutParams.height * 0.8).toInt()
            // 圆点的外边距是圆点容器高度的40%
            val dotMargin = (dotContainer.layoutParams.height * 0.4).toInt()
            for (i in imgUrls.indices) {
                var dotView = ImageView(context)
                var layoutParams = LinearLayout.LayoutParams(dotHeight, dotHeight)
                layoutParams.setMargins(dotMargin, 0, dotMargin, 0)
                dotView.layoutParams = layoutParams
                if (i == 0) {
                    dotView.setBackgroundResource(R.drawable.dot_focus_icon)
                } else {
                    dotView.setBackgroundResource(R.drawable.dot_normal_icon)
                }
                dotContainer.addView(dotView)
            }
            val bannerAdapter = BannerAdapter(imgUrls, context)
            viewPager?.adapter = bannerAdapter
            // 默认选中第一个
            viewPager?.setCurrentItem(1, false)
            startTimer()
        }
    }

    /**
     * 轮播任务处理
     */
    private var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            viewPager.currentItem += 1
//            Log.i(TAG, "after currentItem: " + viewPager.currentItem)
        }
    }

    private var timer: Timer? = null
    /**
     * 开始轮播任务
     */
    private fun startTimer() {
        if (timer == null) {
            timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    mHandler.sendEmptyMessage(1)
                }
            }, duration, duration)
        }
    }

    inner class BannerAdapter(imgUrls: ArrayList<String>, context: Context) : PagerAdapter() {
        private var views: ArrayList<View> = ArrayList()
        private var bitmapManager: BitmapManager = BitmapManager(context)

        init {
            val len = if (imgUrls.size > 1) {
                imgUrls.size + 2
            } else {
                1
            }
            for (i in 0 until len) {
                val imageView = ImageView(context)
                imageView.scaleType = ImageView.ScaleType.FIT_XY // 设置缩放方式
                imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                if (imgUrls.size > 0) {
                    when (i) {
                        0 -> bitmapManager.loadBackgroundBitmap(imgUrls[imgUrls.size - 1], imageView)
                        len - 1 -> bitmapManager.loadBackgroundBitmap(imgUrls[0], imageView)
                        else -> bitmapManager.loadBackgroundBitmap(imgUrls[i - 1], imageView)
                    }
                } else {
                    imageView.setBackgroundResource(R.mipmap.load_default_icon)
                }
                views.add(imageView)
            }
        }

        override fun getCount(): Int {
            return views.size * 100
        }

        override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
//            Log.i(TAG, "isViewFromObject ->  ${view == `object`} ")
            return view == `object`
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
//            Log.i(TAG, "destroyItem -> position: $position ")
            container?.removeView(`object` as View)
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
//            Log.i(TAG, "instantiateItem -> position: $position ")
            val index = position % views.size
            try {
                val pager = container as ViewPager
                pager.addView(views[index])
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }
            return views[index]
        }

        override fun finishUpdate(container: ViewGroup?) {
            val position = viewPager.currentItem
//            Log.i(TAG, "finishUpdate -> position: $position , count: $count")
            if (position == 0) {
                viewPager.setCurrentItem(count - 2 ,false)
            }
            if (position == (count - 1)) {
                viewPager.setCurrentItem(1 ,false)
            }
        }

        override fun setPrimaryItem(container: ViewGroup?, position: Int, `object`: Any?) {
            super.setPrimaryItem(container, position, `object`)
//            Log.i(TAG, "setPrimaryItem -> position: $position")
        }
    }
}