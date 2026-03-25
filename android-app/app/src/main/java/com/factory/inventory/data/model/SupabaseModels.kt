package com.factory.inventory.data.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase 数据模型
 * 用于与 Supabase PostgreSQL 数据库交互
 */

// ==================== 基础数据 ====================

@Serializable
data class SupplierModel(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("contact") val contact: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class CustomerModel(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("contact") val contact: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ItemModel(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("code") val code: String? = null,
    @SerialName("category_id") val categoryId: Long? = null,
    @SerialName("spec") val spec: String? = null,
    @SerialName("unit") val unit: String = "件",
    @SerialName("min_stock") val minStock: Int = 0,
    @SerialName("max_stock") val maxStock: Int? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class WarehouseModel(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("code") val code: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("manager") val manager: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("note") val note: String? = null
)

@Serializable
data class LocationModel(
    @SerialName("id") val id: Long,
    @SerialName("warehouse_id") val warehouseId: Long,
    @SerialName("code") val code: String,
    @SerialName("name") val name: String? = null,
    @SerialName("zone") val zone: String? = null
)

// ==================== 入库相关 ====================

@Serializable
data class InboundOrderModel(
    @SerialName("id") val id: Long,
    @SerialName("order_no") val orderNo: String,
    @SerialName("warehouse_id") val warehouseId: Long,
    @SerialName("supplier_id") val supplierId: Long,
    @SerialName("plate_number") val plateNumber: String? = null,
    @SerialName("driver_name") val driverName: String? = null,
    @SerialName("driver_phone") val driverPhone: String? = null,
    @SerialName("total_amount") val totalAmount: Double = 0.0,
    @SerialName("status") val status: String = "pending",
    @SerialName("operator_id") val operatorId: String? = null,
    @SerialName("order_date") val orderDate: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("completed_at") val completedAt: String? = null,
    @SerialName("note") val note: String? = null,
    // 关联数据（通过 JOIN 获取）
    @SerialName("suppliers") val supplier: SupplierModel? = null,
    @SerialName("warehouses") val warehouse: WarehouseModel? = null
) {
    val supplierName: String get() = supplier?.name ?: ""
    val warehouseName: String get() = warehouse?.name ?: ""
}

@Serializable
data class InboundItemModel(
    @SerialName("id") val id: Long,
    @SerialName("order_id") val orderId: Long,
    @SerialName("item_id") val itemId: Long,
    @SerialName("location_id") val locationId: Long? = null,
    @SerialName("quantity") val quantity: Int,
    @SerialName("unit") val unit: String,
    @SerialName("gross_weight") val grossWeight: Double? = null,
    @SerialName("tare_weight") val tareWeight: Double? = null,
    @SerialName("net_weight") val netWeight: Double? = null,
    @SerialName("weight_unit") val weightUnit: String = "吨",
    @SerialName("unit_price") val unitPrice: Double = 0.0,
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("batch_no") val batchNo: String? = null,
    @SerialName("production_date") val productionDate: String? = null,
    @SerialName("expiry_date") val expiryDate: String? = null,
    @SerialName("note") val note: String? = null
)

@Serializable
data class InboundOrderRequestModel(
    @SerialName("warehouse_id") val warehouseId: Long,
    @SerialName("supplier_id") val supplierId: Long,
    @SerialName("plate_number") val plateNumber: String?,
    @SerialName("driver_name") val driverName: String?,
    @SerialName("driver_phone") val driverPhone: String?,
    @SerialName("items") val items: List<InboundItemRequestModel>,
    @SerialName("note") val note: String?
)

@Serializable
data class InboundItemRequestModel(
    @SerialName("item_id") val itemId: Long,
    @SerialName("location_id") val locationId: Long?,
    @SerialName("quantity") val quantity: Int,
    @SerialName("unit") val unit: String,
    @SerialName("gross_weight") val grossWeight: Double?,
    @SerialName("tare_weight") val tareWeight: Double?,
    @SerialName("net_weight") val netWeight: Double?,
    @SerialName("weight_unit") val weightUnit: String,
    @SerialName("unit_price") val unitPrice: Double,
    @SerialName("batch_no") val batchNo: String?,
    @SerialName("note") val note: String?
)

// ==================== 出库相关 ====================

@Serializable
data class OutboundOrderModel(
    @SerialName("id") val id: Long,
    @SerialName("order_no") val orderNo: String,
    @SerialName("warehouse_id") val warehouseId: Long,
    @SerialName("customer_id") val customerId: Long,
    @SerialName("plate_number") val plateNumber: String? = null,
    @SerialName("driver_name") val driverName: String? = null,
    @SerialName("driver_phone") val driverPhone: String? = null,
    @SerialName("total_amount") val totalAmount: Double = 0.0,
    @SerialName("status") val status: String = "pending",
    @SerialName("operator_id") val operatorId: String? = null,
    @SerialName("order_date") val orderDate: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("completed_at") val completedAt: String? = null,
    @SerialName("note") val note: String? = null,
    // 关联数据
    @SerialName("customers") val customer: CustomerModel? = null,
    @SerialName("warehouses") val warehouse: WarehouseModel? = null
) {
    val customerName: String get() = customer?.name ?: ""
    val warehouseName: String get() = warehouse?.name ?: ""
}

@Serializable
data class OutboundItemModel(
    @SerialName("id") val id: Long,
    @SerialName("order_id") val orderId: Long,
    @SerialName("item_id") val itemId: Long,
    @SerialName("location_id") val locationId: Long? = null,
    @SerialName("quantity") val quantity: Int,
    @SerialName("unit") val unit: String,
    @SerialName("unit_price") val unitPrice: Double = 0.0,
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("batch_no") val batchNo: String? = null,
    @SerialName("note") val note: String? = null
)

@Serializable
data class OutboundOrderRequestModel(
    @SerialName("warehouse_id") val warehouseId: Long,
    @SerialName("customer_id") val customerId: Long,
    @SerialName("plate_number") val plateNumber: String?,
    @SerialName("driver_name") val driverName: String?,
    @SerialName("driver_phone") val driverPhone: String?,
    @SerialName("items") val items: List<OutboundItemRequestModel>,
    @SerialName("note") val note: String?
)

@Serializable
data class OutboundItemRequestModel(
    @SerialName("item_id") val itemId: Long,
    @SerialName("location_id") val locationId: Long?,
    @SerialName("quantity") val quantity: Int,
    @SerialName("unit") val unit: String,
    @SerialName("unit_price") val unitPrice: Double,
    @SerialName("batch_no") val batchNo: String?,
    @SerialName("note") val note: String?
)

// ==================== 库存相关 ====================

@Serializable
data class InventoryModel(
    @SerialName("id") val id: Long,
    @SerialName("item_id") val itemId: Long,
    @SerialName("warehouse_id") val warehouseId: Long,
    @SerialName("location_id") val locationId: Long? = null,
    @SerialName("quantity") val quantity: Int = 0,
    @SerialName("unit") val unit: String,
    @SerialName("batch_no") val batchNo: String? = null,
    @SerialName("last_inbound") val lastInbound: String? = null,
    @SerialName("last_outbound") val lastOutbound: String? = null,
    @SerialName("updated_at") val updatedAt: String,
    // 关联数据
    @SerialName("items") val item: ItemModel? = null,
    @SerialName("warehouses") val warehouse: WarehouseModel? = null
) {
    val itemName: String get() = item?.name ?: ""
    val itemCode: String? get() = item?.code
    val warehouseName: String get() = warehouse?.name ?: ""
    val minStock: Int get() = item?.minStock ?: 0
    val isLow: Boolean get() = quantity < minStock
}

// ==================== 统计相关 ====================

@Serializable
data class StatsModel(
    @SerialName("inbound_today") val inboundToday: Double = 0.0,
    @SerialName("outbound_today") val outboundToday: Double = 0.0,
    @SerialName("inbound_month") val inboundMonth: Double = 0.0,
    @SerialName("outbound_month") val outboundMonth: Double = 0.0,
    @SerialName("profit_today") val profitToday: Double = 0.0,
    @SerialName("profit_month") val profitMonth: Double = 0.0,
    @SerialName("total_items") val totalItems: Int = 0,
    @SerialName("low_stock_count") val lowStockCount: Int = 0
)

// ==================== 用户认证 ====================

@Serializable
data class UserProfileModel(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("real_name") val realName: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("role") val role: String = "operator",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class SupabaseLoginRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class SignUpRequestModel(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("username") val username: String,
    @SerialName("real_name") val realName: String? = null,
    @SerialName("phone") val phone: String? = null
)

// ==================== 通用响应 ====================

@Serializable
data class SupabaseResponse<T>(
    @SerialName("data") val data: T? = null,
    @SerialName("error") val error: String? = null
)
