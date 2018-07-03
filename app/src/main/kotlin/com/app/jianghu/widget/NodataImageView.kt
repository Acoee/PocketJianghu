package com.app.jianghu.widget

import android.content.Context
import android.view.View
import android.widget.ImageView
import cn.com.acoe.jianghu.app.R

/**
 * Created by Acoe on 2018/1/30.
 */
class NodataImageView: ImageView {
    constructor(context: Context): super(context) {
        setBackgroundResource(R.mipmap.no_content_bg)
    }

    public fun show() {
        visibility = View.VISIBLE
    }

    public fun hide() {
        visibility = View.GONE
    }
}