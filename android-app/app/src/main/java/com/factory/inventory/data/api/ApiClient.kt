package com.factory.inventory.data.api

import com.factory.inventory.util.Config
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * API 客户端
 */
object ApiClient {
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService
    
    // Token 拦截器
    private val authInterceptor = AuthInterceptor()
    
    fun init() {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .connectTimeout(Config.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Config.READ_TIMEOUT, TimeUnit.SECONDS)
            .build()
        
        retrofit = Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        apiService = retrofit.create(ApiService::class.java)
    }
    
    fun getService(): ApiService {
        if (!::retrofit.isInitialized) {
            init()
        }
        return apiService
    }
    
    fun setToken(token: String) {
        authInterceptor.setToken(token)
    }
    
    fun clearToken() {
        authInterceptor.clearToken()
    }
}
