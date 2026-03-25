package com.factory.inventory.util

import com.factory.inventory.BuildConfig

/**
 * 应用配置
 */
object Config {
    // ==================== 网络配置 ====================
    
    // 后端模式：true=Supabase, false=Flask
    const val USE_SUPABASE = false  // 切换为 true 使用 Supabase
    const val USE_LOCAL_DATA = !USE_SUPABASE  // 本地测试数据
    
    // Flask 后端地址
    const val BASE_URL = BuildConfig.BASE_URL
    
    // Supabase 配置
    const val SUPABASE_URL = BuildConfig.SUPABASE_URL
    const val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY
    
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
}
