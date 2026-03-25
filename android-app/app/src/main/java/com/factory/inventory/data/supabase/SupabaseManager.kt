package com.factory.inventory.data.supabase

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Supabase 客户端管理器（占位实现）
 * 
 * 注意：Supabase 集成暂时禁用
 * 如需启用，请参考 SUPABASE_QUICKSTART.md 配置依赖
 */
object SupabaseManager {
    
    private var isInitialized = false
    
    /**
     * 初始化 Supabase 客户端
     */
    fun init(context: Context) {
        isInitialized = true
    }
    
    /**
     * 检查是否已初始化
     */
    fun isInitialized(): Boolean = isInitialized
    
    // ==================== 认证相关（占位） ====================
    
    /**
     * 邮箱密码登录
     */
    suspend fun loginWithEmail(email: String, password: String) {
        throw NotImplementedError("Supabase 集成已禁用")
    }
    
    /**
     * 手机号密码登录
     */
    suspend fun loginWithPhone(phone: String, password: String) {
        throw NotImplementedError("Supabase 集成已禁用")
    }
    
    /**
     * 退出登录
     */
    suspend fun logout() {
        throw NotImplementedError("Supabase 集成已禁用")
    }
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean = false
    
    /**
     * 获取当前用户 ID
     */
    fun getCurrentUserId(): String? = null
    
    // ==================== 基础数据查询（占位） ====================
    
    /**
     * 获取供应商列表
     */
    suspend fun getSuppliers(): List<SupplierModel> = emptyList()
    
    /**
     * 获取客户列表
     */
    suspend fun getCustomers(): List<CustomerModel> = emptyList()
    
    /**
     * 获取物品列表
     */
    suspend fun getItems(): List<ItemModel> = emptyList()
    
    /**
     * 获取仓库列表
     */
    suspend fun getWarehouses(): List<WarehouseModel> = emptyList()
    
    // ==================== 入库管理（占位） ====================
    
    /**
     * 获取入库单列表
     */
    suspend fun getInboundList(
        page: Int = 1,
        perPage: Int = 20,
        status: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): List<InboundOrderModel> = emptyList()
    
    /**
     * 创建入库单
     */
    suspend fun createInboundOrder(request: InboundOrderRequestModel): Result<String> {
        return Result.failure(Exception("Supabase 集成已禁用"))
    }
    
    // ==================== 出库管理（占位） ====================
    
    /**
     * 获取出库单列表
     */
    suspend fun getOutboundList(
        page: Int = 1,
        perPage: Int = 20
    ): List<OutboundOrderModel> = emptyList()
    
    /**
     * 创建出库单
     */
    suspend fun createOutboundOrder(request: OutboundOrderRequestModel): Result<String> {
        return Result.failure(Exception("Supabase 集成已禁用"))
    }
    
    // ==================== 库存管理（占位） ====================
    
    /**
     * 获取库存列表
     */
    suspend fun getInventory(
        warehouseId: Long? = null,
        lowStock: Boolean = false
    ): List<InventoryModel> = emptyList()
    
    /**
     * 监听库存变化（Realtime）
     */
    fun listenInventoryChanges(warehouseId: Long? = null): Flow<List<InventoryModel>> = flow {
        emit(emptyList())
    }
    
    // ==================== 统计（占位） ====================
    
    /**
     * 获取统计数据
     */
    suspend fun getStats(): StatsModel = StatsModel()
}
