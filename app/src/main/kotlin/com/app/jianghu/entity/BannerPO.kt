package com.app.jianghu.entity

import java.util.*

/**
 * Banner广告实体类
 */
data class BannerPO(val id: Long, val cateId: Long, val rollName: String, val endTime: Date,
                    val imgPath: String, val imgAlt: String, val linkUrl: String, val sortNo: Short,
                    val status: Short, val upTime: Date, val downTime: Date, val deleteFlag: Short,
                    val createTime: Date, val createId: Long, val updateTime: Date, val updateId: Long)