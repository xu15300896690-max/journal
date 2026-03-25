package com.factory.inventory

import android.app.Application
import com.factory.inventory.data.supabase.SupabaseManager
import com.factory.inventory.util.Config

/**
 * 应用全局上下文
 */
class InventoryApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化 Supabase 客户端
        if (Config.USE_SUPABASE) {
            SupabaseManager.init(applicationContext)
        }
    }
}
