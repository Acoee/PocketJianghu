package com.app.jianghu.ui.start

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.FrameLayout.LayoutParams
import cn.com.acoe.jianghu.app.R
import com.app.jianghu.app.AppConfig
import com.app.jianghu.common.AppShortCutUtil
import com.app.jianghu.ui.MainActivity
import com.app.jianghu.ui.base.BaseActivity

/**
 * 启动页
 * Created by Acoe on 2018/1/29.
 */
class StartActivity : BaseActivity() {
    private val TAG = this.javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 创建应用的快捷方式
        /*val appName = resources.getString(R.string.app_name)
        if (!AppShortCutUtil.isInstallShortcut(applicationContext, appName)) {
            AppShortCutUtil.installRawShortCut(applicationContext, StartActivity::class.java, appName, true)
        }*/
        // 反射启动布局页面
        var startView = View(applicationContext)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        startView.layoutParams = params
        startView.setBackgroundResource(R.mipmap.start_bg)
        // 加载启动页面
        setContentView(startView, TAG)
        // 渐变展示启动屏（由暗到明）
        var aa = AlphaAnimation(0.3f, 1.0f)
        aa.duration = 2000 // 动画持续2s
        startView.startAnimation(aa) // 开始动画
        aa.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }
            override fun onAnimationEnd(animation: Animation?) {
                redirectTo() // 动画结束后跳转
            }
            override fun onAnimationStart(animation: Animation?) {
            }
        })
    }

    /**
     * 跳转操作
     */
    private fun redirectTo() {
        // 检查是否是第一次登录
        val spGuide: SharedPreferences = getSharedPreferences(AppConfig.GUIDE_INFO, Context.MODE_PRIVATE)
        val isFirstIn = spGuide.getBoolean(AppConfig.GUIDE_FIRST_IN, true)
        var intent: Intent? = null
        if (isFirstIn) {
            spGuide.edit().putBoolean(AppConfig.GUIDE_FIRST_IN, false).commit()
//            intent = Intent(applicationContext, GuideActivity::class.java) // 默认不要引导页，我讨厌这个东西
            intent = Intent(applicationContext, MainActivity::class.java)
        } else {
            intent = Intent(applicationContext, MainActivity::class.java)
        }
        startActivity(intent)
        finishActivity()
    }
}