package com.app.jianghu.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import cn.com.acoe.jianghu.app.R
import com.app.jianghu.app.AppContext
import com.app.jianghu.common.BitmapManager
import com.app.jianghu.common.network.Urls
import com.app.jianghu.entity.BannerPO
import com.app.jianghu.entity.CommResponse
import com.app.jianghu.manager.HomeDataMgr
import com.app.jianghu.manager.base.NetSourceListener
import com.app.jianghu.ui.base.BaseFragment
import kotlinx.android.synthetic.main.banner_layout.*
import kotlinx.android.synthetic.main.banner_layout.view.*
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : BaseFragment(), View.OnClickListener {
    private val TAG = this.javaClass.canonicalName
    private lateinit var homeDataMgr: HomeDataMgr
    private lateinit var bitmapManager: BitmapManager

    companion object {
        val BANNER_QUERY = 10001
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return onCreateView(R.layout.home_fragment, TAG, inflater!!, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (this::homeDataMgr.isInitialized) return
        homeDataMgr = HomeDataMgr(this)
        bitmapManager = BitmapManager(AppContext.appContext!!, BitmapFactory.decodeResource(context.resources, R.mipmap.load_default_icon))
        smartRefresh.setOnRefreshListener{
            loadData()
        }
        smartRefresh.setOnLoadmoreListener {
            Log.i(TAG, "setOnLoadmoreListener")
            smartRefresh.finishLoadmore(2000)
        }
        smartRefresh.autoRefresh()
        val strs: Array<String> = arrayOf("压岁红包过大年", "金街玉道迎新春", "帮堂狮舞流水宴")
//        textSwitcher.start(strs, null)
    }

    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBack(what: Int, requestCode: Int, cacheCode: Int, data: Any?) {
        super.onBack(what, requestCode, cacheCode, data)
        smartRefresh.finishRefresh()
        when(what) {
            NetSourceListener.WHAT_SUCCESS -> onSuccess(requestCode, data)
            NetSourceListener.WHAT_ERROR -> onError(requestCode, data)
        }
    }

    /**
     * 数据加载
     */
    private fun loadData() {
        var params = HashMap<String, Any>()
        params.put("cateId", 1)
        homeDataMgr.bannerQuery("bannerList", params, BANNER_QUERY)
    }

    private fun onSuccess(requestCode: Int, data: Any?) {
        when(requestCode) {
            BANNER_QUERY -> {
                val bannerResult = data as CommResponse<Any, Any, BannerPO>
                val banners = bannerResult.rows!!
                if (banners.isNotEmpty()) {
                    val imgUrls = ArrayList<String>(banners.size)
                    banners.forEach {
                        imgUrls.add(Urls.Companion.IMG_SHOW_HOST + it.imgPath)
                    }
                    bannerView.start(context, imgUrls, null, 500)
                }
            }
        }
    }

    private fun onError(requestCode: Int, data: Any?) {
        Log.d(TAG, data.toString())
    }
}