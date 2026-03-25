package com.factory.inventory.data.repository

import android.util.Log
import com.factory.inventory.data.supabase.InboundOrder
import com.factory.inventory.data.supabase.Inventory
import com.factory.inventory.data.supabase.OutboundOrder
import com.factory.inventory.data.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.select

/**
 * 库存管理仓库
 * 
 * 负责库存相关操作：
 * - 入库单管理
 * - 出库单管理
 * - 库存查询
 */
object InventoryRepository {
    
    private const val TAG = "InventoryRepository"
    
    /**
     * 获取入库单列表
     */
    suspend fun getInboundList(
        page: Int = 1,
        perPage: Int = 20,
        status: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Result<List<InboundOrder>> {
        return try {
            Log.d(TAG, "获取入库单列表 page=$page")
            
            val from = ((page - 1) * perPage).toLong()
            val to = from + perPage - 1
            
            val result = SupabaseClient.client.postgrest["inbound_orders"]
                .select(
                    columns = Columns.list(
                        "*",
                        "suppliers!inner(name)",
                        "warehouses!inner(name)"
                    )
                ) {
                    if (status != null) {
                        filter { eq("status", status) }
                    }
                    if (startDate != null) {
                        filter { gte("order_date", startDate) }
                    }
                    if (endDate != null) {
                        filter { lte("order_date", endDate) }
                    }
                    order("created_at") { ascending = false }
                    range(from, to)
                }
                .decodeList<InboundOrder>()
            
            Log.d(TAG, "获取到 ${result.size} 个入库单")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取入库单失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 获取出库单列表
     */
    suspend fun getOutboundList(
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<OutboundOrder>> {
        return try {
            Log.d(TAG, "获取出库单列表 page=$page")
            
            val from = ((page - 1) * perPage).toLong()
            val to = from + perPage - 1
            
            val result = SupabaseClient.client.postgrest["outbound_orders"]
                .select(
                    columns = Columns.list(
                        "*",
                        "customers!inner(name)",
                        "warehouses!inner(name)"
                    )
                ) {
                    order("created_at") { ascending = false }
                    range(from, to)
                }
                .decodeList<OutboundOrder>()
            
            Log.d(TAG, "获取到 ${result.size} 个出库单")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取出库单失败：${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * 获取库存列表
     */
    suspend fun getInventory(
        warehouseId: Long? = null,
        lowStock: Boolean = false
    ): Result<List<Inventory>> {
        return try {
            Log.d(TAG, "获取库存列表")
            
            val query = SupabaseClient.client.postgrest["inventory"]
                .select(
                    columns = Columns.list(
                        "*",
                        "items!inner(name, code, min_stock)",
                        "warehouses!inner(name)"
                    )
                ) {
                    if (warehouseId != null) {
                        filter { eq("warehouse_id", warehouseId) }
                    }
                    order("updated_at") { ascending = false }
                }
                .decodeList<Inventory>()
            
            val result = if (lowStock) {
                query.filter { it.isLow }
            } else {
                query
            }
            
            Log.d(TAG, "获取到 ${result.size} 个库存记录")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "获取库存失败：${e.message}", e)
            Result.failure(e)
        }
    }
}
