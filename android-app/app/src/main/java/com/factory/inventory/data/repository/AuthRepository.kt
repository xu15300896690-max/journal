package com.factory.inventory.data.repository

import android.util.Log
import com.factory.inventory.data.supabase.UserProfile

/**
 * 认证仓库（占位实现）
 */
object AuthRepository {
    
    private const val TAG = "AuthRepository"
    
    /**
     * 邮箱密码登录（占位）
     */
    suspend fun loginWithEmail(email: String, password: String): Result<UserProfile> {
        return try {
            Log.d(TAG, "尝试登录：$email")
            // TODO: 实现 Supabase 登录
            Result.success(UserProfile(
                id = "1",
                email = email,
                username = "admin",
                role = "admin"
            ))
        } catch (e: Exception) {
            Log.e(TAG, "登录失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 退出登录（占位）
     */
    suspend fun logout(): Result<Unit> {
        return Result.success(Unit)
    }
    
    /**
     * 检查是否已登录（占位）
     */
    fun isLoggedIn(): Boolean = true
    
    /**
     * 获取当前用户 ID（占位）
     */
    fun getCurrentUserId(): String? = "1"
}
