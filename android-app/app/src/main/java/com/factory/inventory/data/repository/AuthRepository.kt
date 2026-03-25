package com.factory.inventory.data.repository

import android.util.Log
import com.factory.inventory.data.supabase.SupabaseClient
import com.factory.inventory.data.supabase.UserProfile
import kotlinx.coroutines.tasks.await

/**
 * 认证仓库
 * 
 * 负责用户认证相关操作：
 * - 登录
 * - 登出
 * - 获取当前用户信息
 */
object AuthRepository {
    
    private const val TAG = "AuthRepository"
    
    /**
     * 邮箱密码登录
     */
    suspend fun loginWithEmail(email: String, password: String): Result<UserProfile> {
        return try {
            Log.d(TAG, "尝试登录：$email")
            
            // 使用 Supabase GoTrue 登录
            val response = SupabaseClient.getClient().gotrue.loginWithPassword(
                email = email,
                password = password
            )
            
            Log.d(TAG, "登录成功")
            
            // 构建用户资料
            val user = SupabaseClient.getClient().gotrue.currentSessionOrNull()?.user
                ?: throw Exception("登录成功但未获取到用户信息")
            
            val profile = UserProfile(
                id = user.id,
                email = user.email ?: email,
                username = user.userMetadata["username"]?.toStringOrNull(),
                real_name = user.userMetadata["real_name"]?.toStringOrNull(),
                phone = user.phone,
                role = user.userMetadata["role"]?.toStringOrNull() ?: "operator"
            )
            
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(TAG, "登录失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 手机号密码登录
     */
    suspend fun loginWithPhone(phone: String, password: String): Result<UserProfile> {
        return try {
            Log.d(TAG, "尝试登录：$phone")
            
            val response = SupabaseClient.client.gotrue.loginWithPassword(
                email = phone,
                password = password
            )
            
            Log.d(TAG, "登录成功")
            
            val user = SupabaseClient.client.gotrue.currentSessionOrNull()?.user
                ?: throw Exception("登录成功但未获取到用户信息")
            
            val profile = UserProfile(
                id = user.id,
                email = user.email ?: "",
                phone = phone,
                username = user.userMetadata["username"]?.toStringOrNull(),
                real_name = user.userMetadata["real_name"]?.toStringOrNull(),
                role = user.userMetadata["role"]?.toStringOrNull() ?: "operator"
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
    
    /**
     * 获取当前用户资料
     */
    fun getCurrentUser(): UserProfile? {
        val session = SupabaseClient.getClient().gotrue.currentSessionOrNull()
        val user = session?.user ?: return null
        
        return UserProfile(
            id = user.id,
            email = user.email ?: "",
            username = user.userMetadata["username"]?.toStringOrNull(),
            real_name = user.userMetadata["real_name"]?.toStringOrNull(),
            phone = user.phone,
            role = user.userMetadata["role"]?.toStringOrNull() ?: "operator"
        )
    }
}

// 辅助函数：将 Any? 转换为 String?
private fun Any?.toStringOrNull(): String? = this?.toString()
