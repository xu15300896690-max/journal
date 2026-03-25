package com.factory.inventory.util

import com.factory.inventory.BuildConfig

/**
 * 应用配置
 * 
 * ✅ 完全使用 Supabase 云端数据库
 * ❌ Flask 后端已移除
 */
object Config {
    // ==================== Supabase 配置 ====================
    
    const val SUPABASE_URL = BuildConfig.SUPABASE_URL
    const val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY
    
    // ==================== 功能开关 ====================
    
    // 扫码功能
    const val ENABLE_BARCODE_SCAN = true
    
    // 拍照上传
    const val ENABLE_CAMERA_UPLOAD = true
    
    // 打印功能
    const val ENABLE_PRINT = false
    
    // 离线模式
    const val ENABLE_OFFLINE_MODE = false
    
    // 实时同步（Supabase Realtime）
    const val ENABLE_REALTIME = true
    
    // ==================== 调试选项 ====================
    
    // 显示日志
    const val DEBUG_LOGS = true
}
