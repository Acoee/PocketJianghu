package com.app.jianghu.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import cn.com.acoe.jianghu.app.R
import com.app.jianghu.app.AppManager
import com.app.jianghu.manager.base.AbstractDataManager
import com.app.jianghu.manager.base.DataManagerCallback
import com.app.jianghu.manager.base.NetSourceListener
import com.app.jianghu.widget.LoadingView
import com.app.jianghu.widget.NodataImageView

/**
 * Created by Acoe on 2018/1/19.
 */
open class BaseFragmentActivity : FragmentActivity(), DataManagerCallback {
    protected lateinit var context: BaseFragmentActivity // 当前上下文
    lateinit var nowFragment: String // 当前Fragment
    private lateinit var loadingView: LoadingView // 加载提示框
    private lateinit var imgNodata: NodataImageView // 无数据背景
    private lateinit var activityName: String // 当前Activity名称
    var fragments = HashMap<String, Fragment>() // 主页面内容
    var isShowNodatas = ArrayList<Boolean>() // 是否显示暂无数据背景

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
        AppManager.getAppManager()?.addActivity(this)
    }

    protected fun setContentView(layoutResID: Int, activityName: String) {
        super.setContentView(layoutResID)
        setContentView(layoutResID)
        this.activityName = activityName
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

    fun finishActivity() {
        AppManager.getAppManager()?.finishActivity(this)
    }

    /**
     * 设置初始显示页面
     */
    fun setFirstFragment(key: String) {
        nowFragment = key
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentContainer, fragments[key]).commitAllowingStateLoss()
    }

    /**
     * 切换Fragment显示
     */
    fun changeFragment(key: String) {
        nowFragment = key
        hideLoadingView() // 切换Fragment时把加载框隐藏
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction() // 通过获取默认的FragmentManager来获取Fragment的事务类
        transaction.setCustomAnimations(R.anim.fragment_fading_in, R.anim.fragment_fading_out) // 切换动画
        transaction.replace(R.id.fragmentContainer, fragments[key]) // 页面切换，并加载到中间主体内容中
        transaction.addToBackStack(null) // 被切换的子页面添加的栈底，否则被替换的子页面将完全销毁，下次调用会重新绘制
        transaction.commitAllowingStateLoss() // 切换后需要事务提交才能生效
        /**
         * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
         * 会在进程的主线程中，用异步的方式来执行。
         * 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
         * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
         */
        supportFragmentManager.executePendingTransactions()
    }

    /**
     * 显示加载框
     */
    protected fun showLoadingView() {
        if (isFinishing) return
        if (!this::loadingView.isInitialized || loadingView  == null) {
            loadingView = LoadingView(applicationContext)
            var params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.CENTER
            addContentView(loadingView, params)
        }
        loadingView.show()
    }

    /**
     * 隐藏加载框
     */
    protected fun hideLoadingView() {
        if (isFinishing) return
        if (this::loadingView.isInitialized) {
            loadingView?.hide()
        }
    }

    /**
     * 加载框是否正在显示
     */
    protected fun isLoadingViewShowing(): Boolean {
        if (this::loadingView.isInitialized) {
            return loadingView?.visibility == View.VISIBLE
        }
        return false
    }

    /**
     * 显示暂无数据背景
     */
    fun showNodataImageBg(pos: Int) {
        if (isFinishing) return
        if (!this::imgNodata.isInitialized) {
            imgNodata = NodataImageView(applicationContext)
            var params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            params.gravity = Gravity.CENTER
            addContentView(imgNodata, params)
        }
        imgNodata.show()
        isShowNodatas[pos] = true
    }

    /**
     * 隐藏暂无数据背景
     */
    fun hideNodataImageBg(pos: Int) {
        if (isFinishing) return
        if (this::imgNodata.isInitialized && imgNodata != null) {
            imgNodata.hide()
            isShowNodatas[pos] = false
        }
    }

}