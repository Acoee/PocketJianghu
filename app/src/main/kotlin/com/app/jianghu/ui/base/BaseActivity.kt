package com.app.jianghu.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout.LayoutParams
import android.widget.Toast
import com.app.jianghu.app.AppManager
import com.app.jianghu.manager.base.AbstractDataManager
import com.app.jianghu.manager.base.DataManagerCallback
import com.app.jianghu.manager.base.NetSourceListener
import com.app.jianghu.widget.LoadingView
import com.app.jianghu.widget.NodataImageView

/**
 * Activity基类
 * Created by Acoe on 2018/1/19.
 */
open class BaseActivity : Activity(), DataManagerCallback {

    protected lateinit var context: BaseActivity // 当前上下文
    private lateinit var loadingView: LoadingView // 加载提示框
    private lateinit var imgNodata: NodataImageView // 无数据背景
    private lateinit var activityName: String // 当前Activitty名称

    override fun onBack(what: Int, requestCode: Int, cacheCode: Int, data: Any?) {
        if (isFinishing) return
        when (what) {
            NetSourceListener.WHAT_NOT_LOGIN ->  Toast.makeText(this, "需要登录", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
//        window.setBackgroundDrawable(null)
        AppManager.getAppManager()?.addActivity(this) // 将当前Activity加入到APP管理器中

    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
//        window.setBackgroundDrawable(null)
        AppManager.getAppManager()?.addActivity(this) // 将当前Activity加入到APP管理器中
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (loadingView?.visibility == View.VISIBLE) {
                loadingView.hide()
                return true
            }
            AppManager.getAppManager()?.finishActivity(this)
        }
        return super.onKeyDown(keyCode, event)
    }

    protected fun setContentView(layoutResID: Int, activityName: String) {
        setContentView(layoutResID)
        this.activityName = activityName
    }

    protected  fun setContentView(view: View?, activityName: String) {
        setContentView(view)
        this.activityName = activityName
    }

    /**
     * 显示加载框
     */
    protected fun showLoadingView() {
        if (isFinishing) return
        if (this.loadingView == null) {
            this.loadingView = LoadingView(applicationContext)
            var params: LayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.CENTER
            this.addContentView(this.loadingView, params)
        }
        this.loadingView.show()
    }

    /**
     * 隐藏加载框
     */
    protected fun hideLoadingView() {
        if (isFinishing) return
        this.loadingView?.hide()
    }

    /**
     * 加载框是否正在显示
     */
    protected fun isLoadingViewShowing(): Boolean {
        return this.loadingView?.visibility == View.VISIBLE
    }

    fun showNodataImageBg() {
        if (isFinishing) return
        if (this.imgNodata == null) {
            this.imgNodata = NodataImageView(applicationContext)
            var params: LayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            params.gravity = Gravity.CENTER
            addContentView(this.imgNodata, params)
        }
        this.imgNodata.show()
    }

    fun hideNodataImageBg() {
        if (isFinishing) return
        this.imgNodata?.hide()
    }

    fun finishActivity() {
        AppManager.getAppManager()?.finishActivity(this)
    }
}