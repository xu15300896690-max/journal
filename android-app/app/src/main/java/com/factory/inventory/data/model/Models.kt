package com.factory.inventory.data.model

import com.google.gson.annotations.SerializedName

/**
 * Flask 后端 API 数据模型
 * 用于与 Flask/SQLite 后端交互
 */

/**
 * 通用 API 响应
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

/**
 * ID 响应
 */
data class IdResponse(
    val id: Int
)

/**
 * 分页响应
 */
data class PagedResponse<T>(
    val data: List<T>,
    val total: Int,
    val pages: Int
)

// ==================== 认证模型 ====================

/**
 * 登录请求（Flask 后端用）
 */
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserInfo
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val real_name: String? = null,
    val phone: String? = null,
    val role: String = "operator"
)

data class UserInfo(
    val id: Int,
    val username: String,
    val real_name: String?,
    val role: String
)

// ==================== 基础数据 ====================

/**
 * 供应商（Flask 后端用）
 */
data class Supplier(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("contact") val contact: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address") val address: String? = null
)

/**
 * 客户（Flask 后端用）
 */
data class Customer(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("contact") val contact: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address") val address: String? = null
)

/**
 * 物品（Flask 后端用）
 */
data class Item(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String? = null,
    @SerializedName("spec") val spec: String? = null,
    @SerializedName("unit") val unit: String = "件",
    @SerializedName("min_stock") val min_stock: Int = 0
)

/**
 * 仓库（Flask 后端用）
 */
data class Warehouse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String? = null
)

// ==================== 入库相关 ====================

/**
 * 入库单请求（Flask 后端用）
 */
data class InboundOrderRequest(
    @SerializedName("warehouse_id") val warehouse_id: Int,
    @SerializedName("supplier_id") val supplier_id: Int,
    @SerializedName("plate_number") val plate_number: String?,
    @SerializedName("driver_name") val driver_name: String?,
    @SerializedName("driver_phone") val driver_phone: String?,
    @SerializedName("items") val items: List<InboundItemRequest>,
    @SerializedName("note") val note: String?
)

/**
 * 入库明细请求（Flask 后端用）
 */
data class InboundItemRequest(
    @SerializedName("item_id") val item_id: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unit") val unit: String,
    @SerializedName("unit_price") val unit_price: Double,
    @SerializedName("net_weight") val net_weight: Double? = null,
    @SerializedName("weight_unit") val weight_unit: String = "吨"
)

/**
 * 入库单（Flask 后端用）
 */
data class InboundOrder(
    @SerializedName("id") val id: Int,
    @SerializedName("order_no") val order_no: String,
    @SerializedName("supplier_name") val supplier_name: String,
    @SerializedName("warehouse_name") val warehouse_name: String,
    @SerializedName("plate_number") val plate_number: String?,
    @SerializedName("driver_name") val driver_name: String?,
    @SerializedName("total_amount") val total_amount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val created_at: String
)

// ==================== 出库相关 ====================

/**
 * 出库单请求（Flask 后端用）
 */
data class OutboundOrderRequest(
    @SerializedName("warehouse_id") val warehouse_id: Int,
    @SerializedName("customer_id") val customer_id: Int,
    @SerializedName("plate_number") val plate_number: String?,
    @SerializedName("driver_name") val driver_name: String?,
    @SerializedName("driver_phone") val driver_phone: String?,
    @SerializedName("items") val items: List<OutboundItemRequest>,
    @SerializedName("note") val note: String?
)

/**
 * 出库明细请求（Flask 后端用）
 */
data class OutboundItemRequest(
    @SerializedName("item_id") val item_id: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unit") val unit: String,
    @SerializedName("unit_price") val unit_price: Double
)

/**
 * 出库单（Flask 后端用）
 */
data class OutboundOrder(
    @SerializedName("id") val id: Int,
    @SerializedName("order_no") val order_no: String,
    @SerializedName("customer_name") val customer_name: String,
    @SerializedName("warehouse_name") val warehouse_name: String,
    @SerializedName("plate_number") val plate_number: String?,
    @SerializedName("total_amount") val total_amount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val created_at: String
)

// ==================== 库存相关 ====================

/**
 * 库存项（Flask 后端用）
 */
data class InventoryItem(
    @SerializedName("id") val id: Int,
    @SerializedName("item_name") val item_name: String,
    @SerializedName("item_code") val item_code: String? = null,
    @SerializedName("warehouse_name") val warehouse_name: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unit") val unit: String,
    @SerializedName("min_stock") val min_stock: Int,
    @SerializedName("is_low") val is_low: Boolean
)

// ==================== 统计相关 ====================

/**
 * 统计数据（Flask 后端用）
 */
data class Stats(
    @SerializedName("inbound_today") val inbound_today: Double = 0.0,
    @SerializedName("outbound_today") val outbound_today: Double = 0.0,
    @SerializedName("inbound_month") val inbound_month: Double = 0.0,
    @SerializedName("outbound_month") val outbound_month: Double = 0.0,
    @SerializedName("profit_today") val profit_today: Double = 0.0,
    @SerializedName("profit_month") val profit_month: Double = 0.0,
    @SerializedName("total_items") val total_items: Int = 0,
    @SerializedName("low_stock_count") val low_stock_count: Int = 0
)
