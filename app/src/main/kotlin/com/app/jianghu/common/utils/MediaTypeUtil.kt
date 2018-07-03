package com.app.jianghu.common.utils

/**
 * 上传文件IMEI类型获取工具类
 * Created by Acoe on 2018/1/23.
 */
class MediaTypeUtil {
    companion object {
        fun getMimeType(fileName: String): String {
            val suffix: String = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase()
            when (suffix) {
                "HTML" -> return Type.HTML
                "TEXT" -> return  Type.TEXT
                "XML" -> return Type.XML
                "GIF" -> return Type.GIF
                "JEPG" -> return Type.JEPG
                "JSON" -> return Type.JSON
                "PDF" -> return Type.PDF
                "WORD" -> return Type.WORD
                else -> return Type.STREAM
            }
            return suffix
        }
    }

    class Type {
        companion object {
            val HTML = "text/html" // HTML格式
            val TEXT = "text/plain" // 纯文本格式
            val XML = "text/xml" // XML格式
            val GIF = "image/gif" // gif图片格式
            val JEPG = "image/jepg" // jepg图片格式
            val JSON = "application/json" // JSON数据格式
            val PDF = "application/pdf" // pdf格式
            var WORD = "application/msword" // word文档格式
            var STREAM = "application/octet-stream" // 二进制流数据
        }
    }
}
