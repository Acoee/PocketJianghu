package com.app.jianghu.common.network

import android.util.Log
import com.app.jianghu.app.AppConfig
import com.app.jianghu.app.AppConfig.Companion.TAG
import com.app.jianghu.common.utils.ClientInfo
import com.app.jianghu.common.utils.MediaTypeUtil
import okhttp3.*
import org.w3c.dom.Text
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * Created by Acoe on 2018/1/19.
 */
class RemoteRequest {
    companion object {
        private val apiHost = Urls.API_HOST + "/" + Urls.VERSION_NAME + "/" + Urls.ROUTE + "/"
        val REQUEST_FAILURE_MESSAGE = "请求响应失败，请重试！"

        fun callRemoteApi(method: String, params: HashMap<String, Any>?, files: HashMap<String, File>?): String? {
            // 判断网络
            if (ClientInfo.getInstance().mNetworkState == ClientInfo.NETWORK_NONE) {
                return "{\"statusCode\":0,\"message\":\"当前网络不可用，请检查\"}";
                Log.i("RemoteRequest", "当前网络不可用，请检查")
            }
            Log.i(AppConfig.TAG, "访问的URL---------->" + apiHost + method)
            Log.i(AppConfig.TAG, "访问的参数--------->" + params.toString())
            return if (params?.get(Urls.REQUEST_TYPE) == null || Urls.GET.equals(params?.get(Urls.REQUEST_TYPE) as String, true)) {
                doGet(apiHost + method, params)
            } else {
                doPost(apiHost + method, params)
            }
        }

        private fun createSSLSocketFactory(): SSLSocketFactory? {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf(object : X509TrustManager {
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                }

                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }), SecureRandom())
            return sc.socketFactory
        }

        private fun newOkHttpClient(): OkHttpClient {
            when (Urls.HTTP_TYPE == Urls.HTTPS) {
                true -> return OkHttpClient.Builder().sslSocketFactory(createSSLSocketFactory()).hostnameVerifier(object : HostnameVerifier {
                    override fun verify(p0: String?, p1: SSLSession?): Boolean {
                        return true
                    }
                }).build()
                false -> return OkHttpClient()
            }
        }

        private fun doGet(url: String, params: HashMap<String, Any>?): String? {
            var okHttpClient = newOkHttpClient()
            val builder = Request.Builder()
            val urlBuilder = HttpUrl.parse(url)?.newBuilder()
            if (params != null) {
                for (param in params) {
                    urlBuilder?.addQueryParameter(param.key, param.value.toString())
                }
            }
            val request: Request = builder.url(urlBuilder?.build()).get().build()
            val call: Call = okHttpClient.newCall(request)
            var result: String? = null
            Log.i(AppConfig.TAG, "访问的URL---------->" + url)
            try {
                val response = call.execute()
                result = response.body()?.string()
                Log.i(AppConfig.TAG, "返回的结果--------->" + result)
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }
            return result
        }

        private fun doPost(url: String, params: HashMap<String, Any>?): String? {
            var okHttpClient = newOkHttpClient()
            var builder: MultipartBody.Builder = MultipartBody.Builder()
            if (params != null) {
                for (param in params) {
                    if (param.value is File) {
                        val file = param.value as File
                        builder.addFormDataPart(param.key, file.name, RequestBody.create(null, file))
                    } else {
                        builder.addFormDataPart(param.key, param.key)
                    }
                }
            }
            val body: RequestBody = builder.build()
            val request: Request = Request.Builder().url(url).post(body).build()
            val call: Call = okHttpClient.newCall(request)
            var result: String? = null
            Log.i(AppConfig.TAG, "访问的URL---------->" + url)
            try {
                val response = call.execute()
                result = response.body()?.string()
                Log.i(AppConfig.TAG, "返回的结果--------->" + result)
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }
            return result
        }
    }
}