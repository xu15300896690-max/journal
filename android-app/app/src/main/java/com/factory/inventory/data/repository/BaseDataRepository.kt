package com.factory.inventory.data.repository

import android.util.Log
import com.factory.inventory.data.supabase.Customer
import com.factory.inventory.data.supabase.Item
import com.factory.inventory.data.supabase.Supplier
import com.factory.inventory.data.supabase.SupabaseClient
import com.factory.inventory.data.supabase.Warehouse
import io.github.jan.supabase.postgrest.query.select

/**
 * 基础数据仓库
 * 
 * 负责基础数据查询：
 * - 供应商
 * - 客户
 * - 物品
 * - 仓库
 */
object BaseDataRepository {
    
    private const val TAG = "BaseDataRepository"
    
    /**
     * 获取供应商列表
     */
    suspend fun getSuppliers(): Result<List<Supplier>> {
        return try {
            Log.d(TAG, "获取供应商列表")
            
            val result = SupabaseClient.getClient().postgrest["suppliers"]
                .select {
                    order("created_at") { ascending = false }
                }
                .decodeList<Supplier>()
            
            Log.d(TAG, "获取到 ${result.size} 个供应商")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取供应商失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 获取客户列表
     */
    suspend fun getCustomers(): Result<List<Customer>> {
        return try {
            Log.d(TAG, "获取客户列表")
            
            val result = SupabaseClient.getClient().postgrest["customers"]
                .select {
                    order("created_at") { ascending = false }
                }
                .decodeList<Customer>()
            
            Log.d(TAG, "获取到 ${result.size} 个客户")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取客户失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 获取物品列表
     */
    suspend fun getItems(): Result<List<Item>> {
        return try {
            Log.d(TAG, "获取物品列表")
            
            val result = SupabaseClient.getClient().postgrest["items"]
                .select {
                    order("created_at") { ascending = false }
                }
                .decodeList<Item>()
            
            Log.d(TAG, "获取到 ${result.size} 个物品")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取物品失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 获取仓库列表
     */
    suspend fun getWarehouses(): Result<List<Warehouse>> {
        return try {
            Log.d(TAG, "获取仓库列表")
            
            val result = SupabaseClient.getClient().postgrest["warehouses"]
                .select {
                    order("created_at") { ascending = false }
                }
                .decodeList<Warehouse>()
            
            Log.d(TAG, "获取到 ${result.size} 个仓库")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取仓库失败：${e.message}", e)
            Result.failure(e)
        }
    }
}
