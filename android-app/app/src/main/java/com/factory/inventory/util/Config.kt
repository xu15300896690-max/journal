package com.factory.inventory.util

import com.factory.inventory.BuildConfig

/**
 * 应用配置
 */
object Config {
    // Supabase 配置
    const val SUPABASE_URL = BuildConfig.SUPABASE_URL
    const val SUPABASE_ANON_KEY = BuildConfig.SUPABASE_ANON_KEY
    
    // 功能开关
    const val USE_SUPABASE = true
    const val USE_LOCAL_DATA = false
    const val ENABLE_BARCODE_SCAN = true
    const val ENABLE_CAMERA_UPLOAD = true
    const val ENABLE_REALTIME = true
    const val DEBUG_LOGS = true
}
