package com.factory.inventory

import android.app.Application
import com.factory.inventory.data.supabase.SupabaseClient

/**
 * 应用全局上下文
 */
class InventoryApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Supabase 客户端已自动初始化
    }
}
