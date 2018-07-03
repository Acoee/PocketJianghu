package com.app.jianghu.widget

import android.content.Context
import android.view.View
import android.widget.ProgressBar

/**
 * Created by Acoe on 2018/1/29.
 */
class LoadingView: ProgressBar {
    constructor(context: Context): super(context)

    fun show() {
        this.visibility = View.VISIBLE
    }

    fun hide() {
        this.visibility = View.GONE
    }
}