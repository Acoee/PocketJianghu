package com.app.jianghu.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.com.acoe.jianghu.app.R
import com.app.jianghu.ui.base.BaseFragmentActivity
import com.app.jianghu.ui.center.CenterFragment
import com.app.jianghu.ui.home.HomeFragment
import com.app.jianghu.ui.various.VariousFragment
import com.app.jianghu.ui.world.WorldFragment
import kotlinx.android.synthetic.main.main_activity.*

/**
 * Created by Acoe on 2018/1/19.
 */
class MainActivity : BaseFragmentActivity(), View.OnClickListener {
    private val TAG = this.javaClass.name
    private var navActiveIndex = 0 // 当前导航栏索引

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.putInt("navActiveIndex", navActiveIndex)
        outState?.putString("fragment", nowFragment)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity, TAG)
        initUI()
        if (savedInstanceState != null) {
            changeFragment(savedInstanceState.getString("fragment", HomeFragment::class.java.name))
            setNavStyle(savedInstanceState.getInt("navActiveIndex", 0))
        } else {
            setFirstFragment(HomeFragment::class.java.name)
            setNavStyle(0)
        }
    }

    /**
     * 连续按两次返回退出应用
     */
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        var flag = true
        if (event!!.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                moveTaskToBack(true)
            }
        } else {
            flag = super.dispatchKeyEvent(event)
        }
        return flag
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.navHome -> {
                changeFragment(HomeFragment::class.java.name)
                setNavStyle(0)
            }
            R.id.navWorld -> {
                changeFragment(WorldFragment::class.java.name)
                setNavStyle(1)
            }
            R.id.navVarious -> {
                changeFragment(VariousFragment::class.java.name)
                setNavStyle(2)
            }
            R.id.navCenter -> {
                changeFragment(CenterFragment::class.java.name)
                setNavStyle(3)
            }
        }
    }

    private val selectedIds = intArrayOf(R.mipmap.nav_home_selected, R.mipmap.nav_world_selected, R.mipmap.nav_various_selected, R.mipmap.nav_center_selected)
    private val unselectedIds = intArrayOf(R.mipmap.nav_home_unselected, R.mipmap.nav_world_unselected, R.mipmap.nav_various_unselected, R.mipmap.nav_center_unselected)

    /**
     * 设置导航栏样式
     */
    private fun setNavStyle(clickPos: Int) {
        navActiveIndex = clickPos
        for (i in 0..bottomNavLayout.childCount) {
            var viewGroup = bottomNavLayout.getChildAt(i) as ViewGroup?
            var imgView = viewGroup?.getChildAt(0) as ImageView?
            var navText = viewGroup?.getChildAt(1) as TextView?
            if (i ==clickPos) {
                imgView?.setBackgroundResource(selectedIds[i])
                navText?.setTextColor(ContextCompat.getColor(this, R.color.main_color))
            } else {
                imgView?.setBackgroundResource(unselectedIds[i])
                navText?.setTextColor(ContextCompat.getColor(this, R.color.main_nav_text_color))
            }
        }
        // 设置无数据背景
        if (isShowNodatas[clickPos]) {
            showNodataImageBg(clickPos)
        } else {
            hideNodataImageBg(clickPos)
        }
    }

    /**
     * 初始化界面
     */
    private fun initUI() {
        // 初始化Fragment，将类名作为key
        fragments.put(HomeFragment::class.java.name, HomeFragment())
        fragments.put(WorldFragment::class.java.name, WorldFragment())
        fragments.put(VariousFragment::class.java.name, VariousFragment())
        fragments.put(CenterFragment::class.java.name, CenterFragment())
        isShowNodatas.add(false)
        isShowNodatas.add(false)
        isShowNodatas.add(false)
        isShowNodatas.add(false)
        // 设置点击事件
        navHome.setOnClickListener(this)
        navWorld.setOnClickListener(this)
        navVarious.setOnClickListener(this)
        navCenter.setOnClickListener(this)
    }
}