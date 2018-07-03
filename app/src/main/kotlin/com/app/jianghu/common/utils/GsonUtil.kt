package com.app.jianghu.common.utils

import java.lang.reflect.Type

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Gson转换和解析json数据工具类
 */
object GsonUtil {
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create() //Gson对象

    /**
     * 将json字符串转换为对象(+1)
     * @param json
     * @param type
     * @return
     */
    fun <T> getObjectFromJson(json: String, type: Type): T? {
        return if (SystemUtils.isEmpty(json)) {
            null
        } else gson.fromJson<T>(json, type)
    }

    /**
     * 将json字符串转换为对象(+2)
     * @param json
     * @param c
     * @return
     */
    fun <T> getObjectFromJson(json: String, c: Class<T>): T? {
        return if (SystemUtils.isEmpty(json)) {
            null
        } else gson.fromJson(json, c)
    }

    /**
     * 将对象转换成字符串(+1)
     * @param t
     * @return
     */
    fun <T> formatObjectToJson(t: T): String {
        return gson.toJson(t)
    }

    /**
     * 将对象转换成字符串(+2)
     * @param t
     * @return
     */
    fun <T> formatObjectToJson(t: T, type: Type): String {
        return gson.toJson(t, type)
    }


}
