package com.factory.inventory.util

import com.factory.inventory.BuildConfig

/**
 * 应用配置
 */
object Config {
    // ==================== 后端模式 ====================
    
    // true = 使用 Supabase 云端数据库
    // false = 使用 Flask 本地服务器
    const val USE_SUPABASE = true  // ✅ 已启用 Supabase
    
    // 本地测试数据（开发调试用）
    const val USE_LOCAL_DATA = !USE_SUPABASE
    
    // ==================== Supabase 配置 ====================
    
    // ⚠️ 在 build.gradle.kts 中配置实际值
    const val SUPABASE_URL = BuildConfig.SUPABASE_URL
    const val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY
    
    // ==================== Flask 后端配置 ====================
    
    // Flask 服务器地址（本地网络）
    const val BASE_URL = BuildConfig.BASE_URL
    
    // 超时时间（秒）
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    
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
    const val ENABLE_REALTIME = USE_SUPABASE
    
    // ==================== 调试选项 ====================
    
    // 显示日志
    const val DEBUG_LOGS = true
    
    // 网络请求日志
    const val HTTP_LOGS = true
}
