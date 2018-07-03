package com.app.jianghu.common.network

/**
 * Created by Acoe on 2018/1/19.
 */
class Urls {
    companion object {
        val API = "PRO" // 测试 TEST，预发 PRE，正式 PRO
        var API_HOST = "http://webapi.jumorenews.test"
        val VERSION_NAME = "v1"
        val ROUTE = "app"
        val REQUEST_TYPE = "requestType"
        val GET = "get"
        val POST = "post"
        var UPLOAD_HOST = "http://uploadapi.jumore.test"
        var IMG_SHOW_HOST = "http://image.jumore.test"
        var HTTP_TYPE = "http" // 网络访问类型
        val HTTP = "http"
        val HTTPS = "https"
    }

    init {
        when (API) {
            "DEV" -> {
                API_HOST = "http://192.168.23.118:8081/v1/"
                UPLOAD_HOST = "http://uploadapi.jumore.test"
                IMG_SHOW_HOST = "http://image.jumore.test"
            }
            "TEST" -> {
                API_HOST = "http://webapi.jumorenews.test"
                UPLOAD_HOST = "http://uploadapi.jumore.test"
                IMG_SHOW_HOST = "http://image.jumore.test"
            }
            "PRE" -> {
                API_HOST = "http://webapi.jumorenews.com.pre"
                UPLOAD_HOST = "http://file1.uploadapi.jumore.com.pre"
                IMG_SHOW_HOST = "http://img.jumore.com.pre"
            }
            "PRO" -> {
                API_HOST = "https://webapi.jumorenews.com"
                UPLOAD_HOST = "http://file1.uploadapi.jumore.com"
                IMG_SHOW_HOST = "http://img.jumore.com"
                HTTP_TYPE = "https"
            }
        }
    }
}