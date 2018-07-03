package com.app.jianghu.entity

data class CommResponse<K, E, T>(var statusCode: Int = 0, var message: String? = null, var data: K? = null, var dataExtend: E? = null,
                           var rows: List<T>? = null)