package com.factory.inventory.data.supabase

import android.content.Context
import android.util.Log
import com.factory.inventory.util.Config
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

/**
 * Supabase 客户端单例
 * 
 * ✅ 完全使用 Supabase 云端数据库 v3.4.1
 * ✅ 已移除 Flask 后端
 */
object SupabaseClient {
    
    private const val TAG = "SupabaseClient"
    
    private const val TAG = "SupabaseClient"
    
    // Supabase 客户端实例
    private val client = createSupabaseClient(
        supabaseUrl = Config.SUPABASE_URL,
        supabaseKey = Config.SUPABASE_ANON_KEY
    )
    
    /**
     * 获取客户端实例
     */
    fun getClient() = client
    
    /**
     * 检查是否已初始化
     */
    fun isInitialized(): Boolean = true
    
    /**
     * 获取客户端实例
     */
    fun getClient() = client
}

// ==================== 数据模型 ====================

/**
 * 供应商
 */
@Serializable
data class Supplier(
    val id: Long,
    val name: String,
    val contact: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val created_at: String? = null
)

/**
 * 客户
 */
@Serializable
data class Customer(
    val id: Long,
    val name: String,
    val contact: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val created_at: String? = null
)

/**
 * 物品/物料
 */
@Serializable
data class Item(
    val id: Long,
    val name: String,
    val code: String? = null,
    val spec: String? = null,
    val unit: String = "件",
    val min_stock: Int = 0,
    val created_at: String? = null
)

/**
 * 仓库
 */
@Serializable
data class Warehouse(
    val id: Long,
    val name: String,
    val code: String? = null,
    val address: String? = null,
    val manager: String? = null,
    val phone: String? = null
)

/**
 * 入库单
 */
@Serializable
data class InboundOrder(
    val id: Long,
    val order_no: String,
    val warehouse_id: Long,
    val supplier_id: Long,
    val plate_number: String? = null,
    val driver_name: String? = null,
    val driver_phone: String? = null,
    val total_amount: Double = 0.0,
    val status: String = "pending",
    val order_date: String? = null,
    val created_at: String,
    val completed_at: String? = null,
    val note: String? = null,
    // 关联数据（通过 JOIN 获取）
    val suppliers: Supplier? = null,
    val warehouses: Warehouse? = null
) {
    val supplierName: String get() = suppliers?.name ?: ""
    val warehouseName: String get() = warehouses?.name ?: ""
}

/**
 * 出库单
 */
@Serializable
data class OutboundOrder(
    val id: Long,
    val order_no: String,
    val warehouse_id: Long,
    val customer_id: Long,
    val plate_number: String? = null,
    val driver_name: String? = null,
    val driver_phone: String? = null,
    val total_amount: Double = 0.0,
    val status: String = "pending",
    val order_date: String? = null,
    val created_at: String,
    val completed_at: String? = null,
    val note: String? = null,
    // 关联数据
    val customers: Customer? = null,
    val warehouses: Warehouse? = null
) {
    val customerName: String get() = customers?.name ?: ""
    val warehouseName: String get() = warehouses?.name ?: ""
}

/**
 * 库存
 */
@Serializable
data class Inventory(
    val id: Long,
    val item_id: Long,
    val warehouse_id: Long,
    val quantity: Int = 0,
    val unit: String = "件",
    val batch_no: String? = null,
    val updated_at: String,
    // 关联数据
    val items: Item? = null,
    val warehouses: Warehouse? = null
) {
    val itemName: String get() = items?.name ?: ""
    val itemCode: String? get() = items?.code
    val warehouseName: String get() = warehouses?.name ?: ""
    val minStock: Int get() = items?.min_stock ?: 0
    val isLow: Boolean get() = quantity < minStock
}

/**
 * 统计数据
 */
@Serializable
data class Stats(
    val inbound_today: Double = 0.0,
    val outbound_today: Double = 0.0,
    val inbound_month: Double = 0.0,
    val outbound_month: Double = 0.0,
    val profit_today: Double = 0.0,
    val profit_month: Double = 0.0,
    val total_items: Int = 0,
    val low_stock_count: Int = 0
)

/**
 * 用户资料
 */
@Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val username: String? = null,
    val real_name: String? = null,
    val phone: String? = null,
    val role: String = "operator"
)
