package com.factory.inventory.util

import com.factory.inventory.InventoryApplication

/**
 * 应用配置
 */
object Config {
    // ⚠️ 修改为你的服务器地址
    const val BASE_URL = "http://192.168.1.100:5000"
    
    // 是否使用本地测试数据
    // true = 使用本地 Mock 数据（开发测试）
    // false = 使用服务器数据（生产环境）
    var USE_LOCAL_DATA = true
    
    // 连接超时 (秒)
    const val CONNECT_TIMEOUT = 30L
    
    // 读取超时 (秒)
    const val READ_TIMEOUT = 30L
    
    // Token 存储 Key
    const val PREF_TOKEN = "auth_token"
    const val PREF_USER = "user_info"
    
    // 图片上传目录
    val UPLOAD_DIR by lazy {
        InventoryApplication.instance.getExternalFilesDir("uploads")
    }
}
