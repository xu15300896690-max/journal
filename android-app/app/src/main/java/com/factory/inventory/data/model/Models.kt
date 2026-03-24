package com.factory.inventory.data.model

import com.google.gson.annotations.SerializedName

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

// ==================== 基础数据模型 ====================

data class Supplier(
    val id: Int? = null,
    val name: String,
    val contact: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val note: String? = null
)

data class Customer(
    val id: Int? = null,
    val name: String,
    val contact: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val note: String? = null
)

data class Item(
    val id: Int? = null,
    val name: String,
    val code: String? = null,
    val spec: String? = null,
    val unit: String = "件",
    val min_stock: Int = 0,
    val note: String? = null
)

data class Warehouse(
    val id: Int? = null,
    val name: String,
    val code: String? = null,
    val address: String? = null,
    val manager: String? = null,
    val phone: String? = null
)

// ==================== 入库模型 ====================

data class InboundOrderRequest(
    val warehouse_id: Int,
    val supplier_id: Int,
    val plate_number: String? = null,
    val driver_name: String? = null,
    val driver_phone: String? = null,
    val items: List<InboundItemRequest>,
    val note: String? = null
)

data class InboundItemRequest(
    val item_id: Int,
    val location_id: Int? = null,
    val quantity: Int,
    val unit: String = "件",
    val gross_weight: Double? = null,
    val tare_weight: Double? = null,
    val net_weight: Double? = null,
    val weight_unit: String = "吨",
    val unit_price: Double = 0.0,
    val batch_no: String? = null,
    val note: String? = null
)

data class InboundOrderResponse(
    val order_no: String,
    val id: Int
)

data class InboundOrder(
    val id: Int,
    val order_no: String,
    val supplier_name: String,
    val warehouse_name: String,
    val plate_number: String?,
    val driver_name: String?,
    val total_amount: Double,
    val status: String,
    val order_date: String,
    val created_at: String
)

// ==================== 出库模型 ====================

data class OutboundOrderRequest(
    val warehouse_id: Int,
    val customer_id: Int,
    val plate_number: String? = null,
    val driver_name: String? = null,
    val driver_phone: String? = null,
    val items: List<OutboundItemRequest>,
    val note: String? = null
)

data class OutboundItemRequest(
    val item_id: Int,
    val location_id: Int? = null,
    val quantity: Int,
    val unit: String = "件",
    val unit_price: Double = 0.0,
    val batch_no: String? = null,
    val note: String? = null
)

data class OutboundOrderResponse(
    val order_no: String,
    val id: Int
)

data class OutboundOrder(
    val id: Int,
    val order_no: String,
    val customer_name: String,
    val warehouse_name: String,
    val plate_number: String?,
    val total_amount: Double,
    val status: String,
    val order_date: String,
    val created_at: String
)

// ==================== 库存模型 ====================

data class InventoryItem(
    val id: Int,
    val item_name: String,
    val item_code: String?,
    val warehouse_name: String,
    val quantity: Int,
    val unit: String,
    val min_stock: Int,
    val is_low: Boolean
)

// ==================== 统计模型 ====================

data class Stats(
    val inbound_today: Double,
    val outbound_today: Double,
    val inbound_month: Double,
    val outbound_month: Double,
    val profit_today: Double,
    val profit_month: Double,
    val total_items: Int,
    val low_stock_count: Int
)
