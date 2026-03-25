package com.factory.inventory.data.repository

import android.util.Log
import com.factory.inventory.data.supabase.SupabaseClient
import com.factory.inventory.data.supabase.UserProfile

/**
 * 认证仓库
 */
object AuthRepository {
    
    private const val TAG = "AuthRepository"
    
    /**
     * 邮箱密码登录
     */
    suspend fun loginWithEmail(email: String, password: String): Result<UserProfile> {
        return try {
            Log.d(TAG, "尝试登录：$email")
            
            val response = SupabaseClient.getClient().gotrue.loginWithPassword(
                email = email,
                password = password
            )
            
            Log.d(TAG, "登录成功")
            
            val user = SupabaseClient.getClient().gotrue.currentSessionOrNull()?.user
                ?: throw Exception("登录成功但未获取到用户信息")
            
            val profile = UserProfile(
                id = user.id,
                email = user.email ?: email,
                username = user.userMetadata["username"]?.toString(),
                real_name = user.userMetadata["real_name"]?.toString(),
                phone = user.phone,
                role = user.userMetadata["role"]?.toString() ?: "operator"
            )
            
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(TAG, "登录失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 退出登录
     */
    suspend fun logout(): Result<Unit> {
        return try {
            Log.d(TAG, "退出登录")
            SupabaseClient.getClient().gotrue.logout()
            Log.d(TAG, "退出成功")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "退出失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean {
        return SupabaseClient.getClient().gotrue.currentSessionOrNull() != null
    }
    
    /**
     * 获取当前用户 ID
     */
    fun getCurrentUserId(): String? {
        return SupabaseClient.getClient().gotrue.currentSessionOrNull()?.user?.id
    }
}
