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
 */
object BaseDataRepository {
    
    private const val TAG = "BaseDataRepository"
    
    suspend fun getSuppliers(): Result<List<Supplier>> {
        return try {
            Log.d(TAG, "获取供应商列表")
            val result = SupabaseClient.getClient().postgrest["suppliers"]
                .select()
                .decodeList<Supplier>()
            Log.d(TAG, "获取到 ${result.size} 个供应商")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取供应商失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getCustomers(): Result<List<Customer>> {
        return try {
            Log.d(TAG, "获取客户列表")
            val result = SupabaseClient.getClient().postgrest["customers"]
                .select()
                .decodeList<Customer>()
            Log.d(TAG, "获取到 ${result.size} 个客户")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取客户失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getItems(): Result<List<Item>> {
        return try {
            Log.d(TAG, "获取物品列表")
            val result = SupabaseClient.getClient().postgrest["items"]
                .select()
                .decodeList<Item>()
            Log.d(TAG, "获取到 ${result.size} 个物品")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取物品失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getWarehouses(): Result<List<Warehouse>> {
        return try {
            Log.d(TAG, "获取仓库列表")
            val result = SupabaseClient.getClient().postgrest["warehouses"]
                .select()
                .decodeList<Warehouse>()
            Log.d(TAG, "获取到 ${result.size} 个仓库")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取仓库失败：${e.message}", e)
            Result.failure(e)
        }
    }
}
