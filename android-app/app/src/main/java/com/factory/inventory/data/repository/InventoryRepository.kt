package com.factory.inventory.data.repository

import android.util.Log
import com.factory.inventory.data.supabase.InboundOrder
import com.factory.inventory.data.supabase.Inventory
import com.factory.inventory.data.supabase.OutboundOrder

/**
 * 库存管理仓库（占位实现）
 */
object InventoryRepository {
    
    private const val TAG = "InventoryRepository"
    
    suspend fun getInboundList(page: Int = 1, perPage: Int = 20): Result<List<InboundOrder>> {
        Log.d(TAG, "获取入库单列表")
        // TODO: 实现 Supabase 查询
        return Result.success(emptyList())
    }
    
    suspend fun getOutboundList(page: Int = 1, perPage: Int = 20): Result<List<OutboundOrder>> {
        Log.d(TAG, "获取出库单列表")
        return Result.success(emptyList())
    }
    
    suspend fun getInventory(warehouseId: Long? = null, lowStock: Boolean = false): Result<List<Inventory>> {
        Log.d(TAG, "获取库存列表")
        return Result.success(emptyList())
    }
}
