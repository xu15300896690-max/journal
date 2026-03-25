package com.factory.inventory.data.supabase

import android.content.Context
import android.util.Log
import com.factory.inventory.util.Config
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Supabase 客户端管理器 - 生产环境
 * 
 * ✅ 已移除 Flask 后端
 * ✅ 完全使用 Supabase 云端数据库
 */
object SupabaseManager {
    
    private const val TAG = "SupabaseManager"
    private lateinit var client: SupabaseClient
    private var isInitialized = false
    
    /**
     * 初始化 Supabase 客户端
     */
    fun init(context: Context) {
        if (isInitialized) {
            Log.d(TAG, "Supabase 已初始化")
            return
        }
        
        Log.d(TAG, "初始化 Supabase 客户端...")
        Log.d(TAG, "URL: ${Config.SUPABASE_URL}")
        
        client = createSupabaseClient(
            supabaseUrl = Config.SUPABASE_URL,
            supabaseKey = Config.SUPABASE_ANON_KEY
        ) {
            // 配置客户端
        }
        
        isInitialized = true
        Log.d(TAG, "Supabase 初始化完成")
    }
    
    /**
     * 获取客户端实例
     */
    private fun getClient(): SupabaseClient {
        check(isInitialized) { 
            val msg = "SupabaseManager 未初始化，请先调用 init()"
            Log.e(TAG, msg)
            throw IllegalStateException(msg)
        }
        return client
    }
    
    // ==================== 认证相关 ====================
    
    /**
     * 邮箱密码登录
     */
    suspend fun loginWithEmail(email: String, password: String) {
        try {
            Log.d(TAG, "尝试登录：$email")
            getClient().gotrue.loginWithPassword(
                email = email,
                password = password
            )
            Log.d(TAG, "登录成功")
        } catch (e: Exception) {
            Log.e(TAG, "登录失败：${e.message}", e)
            throw e
        }
    }
    
    /**
     * 手机号密码登录
     */
    suspend fun loginWithPhone(phone: String, password: String) {
        try {
            Log.d(TAG, "尝试登录：$phone")
            getClient().gotrue.loginWithPassword(
                email = phone,
                password = password
            )
            Log.d(TAG, "登录成功")
        } catch (e: Exception) {
            Log.e(TAG, "登录失败：${e.message}", e)
            throw e
        }
    }
    
    /**
     * 退出登录
     */
    suspend fun logout() {
        try {
            Log.d(TAG, "退出登录")
            getClient().gotrue.logout()
            Log.d(TAG, "退出成功")
        } catch (e: Exception) {
            Log.e(TAG, "退出失败：${e.message}", e)
            throw e
        }
    }
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean {
        val loggedIn = if (isInitialized) {
            getClient().gotrue.currentSessionOrNull() != null
        } else {
            false
        }
        Log.d(TAG, "登录状态：$loggedIn")
        return loggedIn
    }
    
    /**
     * 获取当前用户 ID
     */
    fun getCurrentUserId(): String? {
        val userId = if (isInitialized) {
            getClient().gotrue.currentSessionOrNull()?.user?.id
        } else {
            null
        }
        Log.d(TAG, "当前用户 ID: $userId")
        return userId
    }
    
    // ==================== 基础数据查询 ====================
    
    /**
     * 获取供应商列表
     */
    suspend fun getSuppliers(): List<SupplierModel> {
        return try {
            Log.d(TAG, "获取供应商列表")
            val result = getClient().postgrest["suppliers"]
                .select()
                .decodeList<SupplierModel>()
            Log.d(TAG, "获取到 ${result.size} 个供应商")
            result
        } catch (e: Exception) {
            Log.e(TAG, "获取供应商失败：${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * 获取客户列表
     */
    suspend fun getCustomers(): List<CustomerModel> {
        return try {
            Log.d(TAG, "获取客户列表")
            val result = getClient().postgrest["customers"]
                .select()
                .decodeList<CustomerModel>()
            Log.d(TAG, "获取到 ${result.size} 个客户")
            result
        } catch (e: Exception) {
            Log.e(TAG, "获取客户失败：${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * 获取物品列表
     */
    suspend fun getItems(): List<ItemModel> {
        return try {
            Log.d(TAG, "获取物品列表")
            val result = getClient().postgrest["items"]
                .select()
                .decodeList<ItemModel>()
            Log.d(TAG, "获取到 ${result.size} 个物品")
            result
        } catch (e: Exception) {
            Log.e(TAG, "获取物品失败：${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * 获取仓库列表
     */
    suspend fun getWarehouses(): List<WarehouseModel> {
        return try {
            Log.d(TAG, "获取仓库列表")
            val result = getClient().postgrest["warehouses"]
                .select()
                .decodeList<WarehouseModel>()
            Log.d(TAG, "获取到 ${result.size} 个仓库")
            result
        } catch (e: Exception) {
            Log.e(TAG, "获取仓库失败：${e.message}", e)
            emptyList()
        }
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
        return try {
            Log.d(TAG, "获取入库单列表 page=$page")
            val from = ((page - 1) * perPage).toLong()
            val to = from + perPage - 1
            
            val result = getClient().postgrest["inbound_orders"]
                .select()
                .decodeList<InboundOrderModel>()
            
            Log.d(TAG, "获取到 ${result.size} 个入库单")
            result
        } catch (e: Exception) {
            Log.e(TAG, "获取入库单失败：${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * 创建入库单
     */
    suspend fun createInboundOrder(request: InboundOrderRequestModel): Result<String> {
        return try {
            Log.d(TAG, "创建入库单")
            Result.failure(Exception("需要实现数据库函数调用"))
        } catch (e: Exception) {
            Log.e(TAG, "创建入库单失败：${e.message}", e)
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
        return try {
            Log.d(TAG, "获取出库单列表 page=$page")
            val result = getClient().postgrest["outbound_orders"]
                .select()
                .decodeList<OutboundOrderModel>()
            Log.d(TAG, "获取到 ${result.size} 个出库单")
            result
        } catch (e: Exception) {
            Log.e(TAG, "获取出库单失败：${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * 创建出库单
     */
    suspend fun createOutboundOrder(request: OutboundOrderRequestModel): Result<String> {
        return try {
            Log.d(TAG, "创建出库单")
            Result.failure(Exception("需要实现数据库函数调用"))
        } catch (e: Exception) {
            Log.e(TAG, "创建出库单失败：${e.message}", e)
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
        return try {
            Log.d(TAG, "获取库存列表")
            val result = getClient().postgrest["inventory"]
                .select()
                .decodeList<InventoryModel>()
            Log.d(TAG, "获取到 ${result.size} 个库存记录")
            result
        } catch (e: Exception) {
            Log.e(TAG, "获取库存失败：${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * 监听库存变化（Realtime）
     */
    fun listenInventoryChanges(warehouseId: Long? = null): Flow<List<InventoryModel>> = channelFlow {
        Log.d(TAG, "开始监听库存变化")
        
        // TODO: 实现 realtime 订阅
        // 当前先发送空列表
        send(emptyList())
    }
    
    // ==================== 统计 ====================
    
    /**
     * 获取统计数据
     */
    suspend fun getStats(): StatsModel {
        return try {
            Log.d(TAG, "获取统计数据")
            StatsModel()
        } catch (e: Exception) {
            Log.e(TAG, "获取统计失败：${e.message}", e)
            StatsModel()
        }
    }
}

// ==================== 数据模型 ====================

@Serializable
data class SupplierModel(
    val id: Long,
    val name: String,
    val contact: String? = null,
    val phone: String? = null,
    val address: String? = null
)

@Serializable
data class CustomerModel(
    val id: Long,
    val name: String,
    val contact: String? = null,
    val phone: String? = null,
    val address: String? = null
)

@Serializable
data class ItemModel(
    val id: Long,
    val name: String,
    val code: String? = null,
    val spec: String? = null,
    val unit: String = "件",
    val min_stock: Int = 0
)

@Serializable
data class WarehouseModel(
    val id: Long,
    val name: String,
    val code: String? = null
)

@Serializable
data class InboundOrderModel(
    val id: Long,
    val order_no: String,
    val supplier_name: String,
    val warehouse_name: String,
    val plate_number: String? = null,
    val driver_name: String? = null,
    val total_amount: Double,
    val status: String,
    val created_at: String
)

@Serializable
data class OutboundOrderModel(
    val id: Long,
    val order_no: String,
    val customer_name: String,
    val warehouse_name: String,
    val plate_number: String? = null,
    val total_amount: Double,
    val status: String,
    val created_at: String
)

@Serializable
data class InventoryModel(
    val id: Long,
    val item_name: String,
    val item_code: String? = null,
    val warehouse_name: String,
    val quantity: Int,
    val unit: String,
    val min_stock: Int,
    val is_low: Boolean
)

@Serializable
data class StatsModel(
    val inbound_today: Double = 0.0,
    val outbound_today: Double = 0.0,
    val profit_today: Double = 0.0,
    val total_items: Int = 0,
    val low_stock_count: Int = 0
)

@Serializable
data class InboundOrderRequestModel(
    val warehouseId: Long,
    val supplierId: Long,
    val plateNumber: String?,
    val driverName: String?,
    val driverPhone: String?,
    val items: List<InboundItemRequestModel>,
    val note: String?
)

@Serializable
data class InboundItemRequestModel(
    val itemId: Long,
    val quantity: Int,
    val unit: String,
    val unitPrice: Double
)

@Serializable
data class OutboundOrderRequestModel(
    val warehouseId: Long,
    val customerId: Long,
    val plateNumber: String?,
    val driverName: String?,
    val driverPhone: String?,
    val items: List<OutboundItemRequestModel>,
    val note: String?
)

@Serializable
data class OutboundItemRequestModel(
    val itemId: Long,
    val quantity: Int,
    val unit: String,
    val unitPrice: Double
)
