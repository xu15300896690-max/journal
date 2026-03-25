package com.factory.inventory.data.repository

import android.util.Log
import com.factory.inventory.data.supabase.Customer
import com.factory.inventory.data.supabase.Item
import com.factory.inventory.data.supabase.Supplier
import com.factory.inventory.data.supabase.Warehouse

/**
 * 基础数据仓库（占位实现）
 */
object BaseDataRepository {
    
    private const val TAG = "BaseDataRepository"
    
    suspend fun getSuppliers(): Result<List<Supplier>> {
        Log.d(TAG, "获取供应商列表")
        // TODO: 实现 Supabase 查询
        return Result.success(emptyList())
    }
    
    suspend fun getCustomers(): Result<List<Customer>> {
        Log.d(TAG, "获取客户列表")
        return Result.success(emptyList())
    }
    
    suspend fun getItems(): Result<List<Item>> {
        Log.d(TAG, "获取物品列表")
        return Result.success(emptyList())
    }
    
    suspend fun getWarehouses(): Result<List<Warehouse>> {
        Log.d(TAG, "获取仓库列表")
        return Result.success(emptyList())
    }
}
