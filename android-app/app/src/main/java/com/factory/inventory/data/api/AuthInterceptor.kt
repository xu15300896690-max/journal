package com.factory.inventory.data.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Token 认证拦截器
 */
class AuthInterceptor : Interceptor {
    private var token: String? = null
    
    fun setToken(token: String) {
        this.token = token
    }
    
    fun clearToken() {
        this.token = null
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // 登录接口不需要 Token
        if (request.url.encodedPath.contains("/login") || 
            request.url.encodedPath.contains("/register")) {
            return chain.proceed(request)
        }
        
        // 添加 Token 到请求头
        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        
        return chain.proceed(newRequest)
    }
}
