package com.app.jianghu.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentContainer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.jianghu.manager.base.AbstractDataManager
import com.app.jianghu.manager.base.DataManagerCallback
import com.app.jianghu.manager.base.NetSourceListener

/**
 * Created by Acoe on 2018/1/19.
 */
open class BaseFragment : Fragment(), DataManagerCallback {
    protected lateinit var context: BaseFragmentActivity // 当前上下文
    private lateinit var nowView: View // 缓存本页面
    private lateinit var fragmentName: String // 当前Fragment名称

    override fun onBack(what: Int, requestCode: Int, cacheCode: Int, data: Any?) {
        if (isDetached) return //判断界面是否已经关闭
        when (what) {
            NetSourceListener.WHAT_NOT_LOGIN ->  Toast.makeText(context, "需要登录", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        context = activity as BaseFragmentActivity
    }

    fun onCreateView(layoutResId: Int, fragmentName: String, inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.fragmentName = fragmentName
        if (!this::nowView.isInitialized) {
            nowView = inflater.inflate(layoutResId, container, false)
        }
        val parent = nowView.parent as ViewGroup?
        parent?.removeAllViewsInLayout()
        return nowView
    }
}