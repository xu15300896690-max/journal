package com.factory.inventory.data.repository

import android.util.Log
import com.factory.inventory.data.supabase.InboundOrder
import com.factory.inventory.data.supabase.Inventory
import com.factory.inventory.data.supabase.OutboundOrder
import com.factory.inventory.data.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.select

/**
 * 库存管理仓库
 */
object InventoryRepository {
    
    private const val TAG = "InventoryRepository"
    
    suspend fun getInboundList(page: Int = 1, perPage: Int = 20): Result<List<InboundOrder>> {
        return try {
            Log.d(TAG, "获取入库单列表 page=$page")
            val result = SupabaseClient.getClient().postgrest["inbound_orders"]
                .select()
                .decodeList<InboundOrder>()
            Log.d(TAG, "获取到 ${result.size} 个入库单")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取入库单失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getOutboundList(page: Int = 1, perPage: Int = 20): Result<List<OutboundOrder>> {
        return try {
            Log.d(TAG, "获取出库单列表 page=$page")
            val result = SupabaseClient.getClient().postgrest["outbound_orders"]
                .select()
                .decodeList<OutboundOrder>()
            Log.d(TAG, "获取到 ${result.size} 个出库单")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取出库单失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getInventory(warehouseId: Long? = null, lowStock: Boolean = false): Result<List<Inventory>> {
        return try {
            Log.d(TAG, "获取库存列表")
            val result = SupabaseClient.getClient().postgrest["inventory"]
                .select()
                .decodeList<Inventory>()
            Log.d(TAG, "获取到 ${result.size} 个库存记录")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取库存失败：${e.message}", e)
            Result.failure(e)
        }
    }
}
