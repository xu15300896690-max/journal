package com.factory.inventory

import android.app.Application
import com.factory.inventory.data.api.ApiClient

class InventoryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 初始化 API 客户端
        ApiClient.init()
    }
    
    companion object {
        lateinit var instance: InventoryApplication
            private set
    }
}
