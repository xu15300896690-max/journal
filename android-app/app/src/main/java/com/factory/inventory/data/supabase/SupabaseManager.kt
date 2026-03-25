package com.factory.inventory.data.supabase

import android.content.Context
import com.factory.inventory.util.Config
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Supabase 客户端管理器 - 生产环境
 */
object SupabaseManager {
    
    private lateinit var client: SupabaseClient
    private var isInitialized = false
    
    /**
     * 初始化 Supabase 客户端
     */
    fun init(context: Context) {
        if (isInitialized) return
        
        client = createSupabaseClient(
            supabaseUrl = Config.SUPABASE_URL,
            supabaseKey = Config.SUPABASE_ANON_KEY
        ) {
            install(GoTrue)
            install(Postgrest)
            install(Realtime)
        }
        
        isInitialized = true
    }
    
    /**
     * 获取客户端实例
     */
    private fun getClient(): SupabaseClient {
        check(isInitialized) { "SupabaseManager 未初始化" }
        return client
    }
    
    // ==================== 认证相关 ====================
    
    /**
     * 邮箱密码登录
     */
    suspend fun loginWithEmail(email: String, password: String) {
        getClient().gotrue.loginWithPassword(email, password)
    }
    
    /**
     * 手机号密码登录
     */
    suspend fun loginWithPhone(phone: String, password: String) {
        getClient().gotrue.loginWithPassword(phone, password)
    }
    
    /**
     * 退出登录
     */
    suspend fun logout() {
        getClient().gotrue.logout()
    }
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean {
        return if (isInitialized) {
            getClient().gotrue.currentSessionOrNull() != null
        } else {
            false
        }
    }
    
    /**
     * 获取当前用户 ID
     */
    fun getCurrentUserId(): String? {
        return if (isInitialized) {
            getClient().gotrue.currentSessionOrNull()?.user?.id
        } else {
            null
        }
    }
    
    // ==================== 基础数据查询 ====================
    
    /**
     * 获取供应商列表
     */
    suspend fun getSuppliers(): List<SupplierModel> {
        return getClient().postgrest["suppliers"]
            .select {
                order("created_at") { ascending = false }
            }
            .decodeList<SupplierModel>()
    }
    
    /**
     * 获取客户列表
     */
    suspend fun getCustomers(): List<CustomerModel> {
        return getClient().postgrest["customers"]
            .select {
                order("created_at") { ascending = false }
            }
            .decodeList<CustomerModel>()
    }
    
    /**
     * 获取物品列表
     */
    suspend fun getItems(): List<ItemModel> {
        return getClient().postgrest["items"]
            .select {
                order("created_at") { ascending = false }
            }
            .decodeList<ItemModel>()
    }
    
    /**
     * 获取仓库列表
     */
    suspend fun getWarehouses(): List<WarehouseModel> {
        return getClient().postgrest["warehouses"]
            .select {
                order("created_at") { ascending = false }
            }
            .decodeList<WarehouseModel>()
    }
    
    // ==================== 入库管理 ====================
    
    /**
     * 获取入库单列表
     */
    suspend fun getInboundList(
        page: Int = 1,
        perPage: Int = 20,
        status: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): List<InboundOrderModel> {
        val from = ((page - 1) * perPage).toLong()
        val to = from + perPage - 1
        
        return getClient().postgrest["inbound_orders"]
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
            .decodeList<InboundOrderModel>()
    }
    
    /**
     * 创建入库单（调用数据库函数）
     */
    suspend fun createInboundOrder(request: InboundOrderRequestModel): Result<String> {
        return try {
            val json = Json { encodeDefaults = true }
            val itemsJson = json.encodeToJsonElement(request.items).toString()
            
            val result = getClient().postgrest.rpc(
                functionName = "create_inbound_order",
                buildJsonObject {
                    put("p_warehouse_id", request.warehouseId)
                    put("p_supplier_id", request.supplierId)
                    put("p_plate_number", request.plateNumber ?: "")
                    put("p_driver_name", request.driverName ?: "")
                    put("p_driver_phone", request.driverPhone ?: "")
                    put("p_items", itemsJson)
                    put("p_note", request.note ?: "")
                }
            )
            
            val orderNo = result.decodeInboundOrderResult().order_no
            Result.success(orderNo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== 出库管理 ====================
    
    /**
     * 获取出库单列表
     */
    suspend fun getOutboundList(
        page: Int = 1,
        perPage: Int = 20
    ): List<OutboundOrderModel> {
        val from = ((page - 1) * perPage).toLong()
        val to = from + perPage - 1
        
        return getClient().postgrest["outbound_orders"]
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
            .decodeList<OutboundOrderModel>()
    }
    
    /**
     * 创建出库单（调用数据库函数）
     */
    suspend fun createOutboundOrder(request: OutboundOrderRequestModel): Result<String> {
        return try {
            val json = Json { encodeDefaults = true }
            val itemsJson = json.encodeToJsonElement(request.items).toString()
            
            val result = getClient().postgrest.rpc(
                functionName = "create_outbound_order",
                buildJsonObject {
                    put("p_warehouse_id", request.warehouseId)
                    put("p_customer_id", request.customerId)
                    put("p_plate_number", request.plateNumber ?: "")
                    put("p_driver_name", request.driverName ?: "")
                    put("p_driver_phone", request.driverPhone ?: "")
                    put("p_items", itemsJson)
                    put("p_note", request.note ?: "")
                }
            )
            
            val response = result.decodeOutboundOrderResult()
            if (response.success) {
                Result.success(response.order_no)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== 库存管理 ====================
    
    /**
     * 获取库存列表
     */
    suspend fun getInventory(
        warehouseId: Long? = null,
        lowStock: Boolean = false
    ): List<InventoryModel> {
        val query = getClient().postgrest["inventory"]
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
            .decodeList<InventoryModel>()
        
        return if (lowStock) {
            query.filter { it.isLow }
        } else {
            query
        }
    }
    
    /**
     * 监听库存变化（Realtime 实时订阅）
     */
    fun listenInventoryChanges(warehouseId: Long? = null): Flow<List<InventoryModel>> = channelFlow {
        val channel = getClient().realtime.channel("inventory:warehouse_id=eq.$warehouseId")
        
        channel.onPostgresChanges(
            event = io.github.jan.supabase.realtime.PostgresChangeEvent.ALL,
            schema = "public",
            table = "inventory"
        ) {
            // 库存变化时重新加载
            val updatedList = getInventory(warehouseId)
            send(updatedList)
        }
        
        channel.subscribe()
        
        // 发送初始数据
        send(getInventory(warehouseId))
    }
    
    // ==================== 统计 ====================
    
    /**
     * 获取统计数据
     */
    suspend fun getStats(): StatsModel {
        return StatsModel()
    }
}

// ==================== 辅助数据类 ====================

@Serializable
private data class InboundOrderResultModel(
    val order_id: Long,
    val order_no: String
)

@Serializable
private data class OutboundOrderResultModel(
    val order_id: Long,
    val order_no: String,
    val success: Boolean,
    val message: String
)

private fun io.github.jan.supabase.postgrest.query.PostgrestFilterBuilder.decodeInboundOrderResult(): InboundOrderResultModel {
    return Json.decodeFromJsonElement(this.data)
}

private fun io.github.jan.supabase.postgrest.query.PostgrestFilterBuilder.decodeOutboundOrderResult(): OutboundOrderResultModel {
    return Json.decodeFromJsonElement(this.data)
}
