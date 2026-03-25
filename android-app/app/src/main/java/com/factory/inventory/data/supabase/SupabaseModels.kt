package com.factory.inventory.data.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase 数据模型
 */

@Serializable
data class Supplier(
    val id: Long,
    val name: String,
    val contact: String? = null,
    val phone: String? = null,
    val address: String? = null
)

@Serializable
data class Customer(
    val id: Long,
    val name: String,
    val contact: String? = null,
    val phone: String? = null,
    val address: String? = null
)

@Serializable
data class Item(
    val id: Long,
    val name: String,
    val code: String? = null,
    val spec: String? = null,
    val unit: String = "件",
    val min_stock: Int = 0
)

@Serializable
data class Warehouse(
    val id: Long,
    val name: String,
    val code: String? = null
)

@Serializable
data class InboundOrder(
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
data class OutboundOrder(
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
data class Inventory(
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
data class Stats(
    val inbound_today: Double = 0.0,
    val outbound_today: Double = 0.0,
    val profit_today: Double = 0.0,
    val total_items: Int = 0,
    val low_stock_count: Int = 0
)

@Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val username: String? = null,
    val real_name: String? = null,
    val phone: String? = null,
    val role: String = "operator"
)
