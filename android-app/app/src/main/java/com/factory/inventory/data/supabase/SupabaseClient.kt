package com.factory.inventory.data.supabase

import com.factory.inventory.util.Config
import io.github.jan.supabase.createSupabaseClient

/**
 * Supabase 客户端单例
 */
object SupabaseClient {
    
    // Supabase 客户端实例
    private val client = createSupabaseClient(
        supabaseUrl = Config.SUPABASE_URL,
        supabaseKey = Config.SUPABASE_ANON_KEY
    ) { }
    
    /**
     * 获取客户端实例
     */
    fun getClient() = client
}
