package com.factory.inventory.data.api

import com.factory.inventory.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API 服务接口
 */
interface ApiService {
    
    // ==================== 认证 ====================
    
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>
    
    @POST("/api/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Unit>>
    
    // ==================== 基础数据 ====================
    
    @GET("/api/suppliers")
    suspend fun getSuppliers(): Response<ApiResponse<List<Supplier>>>
    
    @POST("/api/suppliers")
    suspend fun addSupplier(@Body supplier: Supplier): Response<ApiResponse<IdResponse>>
    
    @GET("/api/customers")
    suspend fun getCustomers(): Response<ApiResponse<List<Customer>>>
    
    @POST("/api/customers")
    suspend fun addCustomer(@Body customer: Customer): Response<ApiResponse<IdResponse>>
    
    @GET("/api/items")
    suspend fun getItems(): Response<ApiResponse<List<Item>>>
    
    @POST("/api/items")
    suspend fun addItem(@Body item: Item): Response<ApiResponse<IdResponse>>
    
    @GET("/api/warehouses")
    suspend fun getWarehouses(): Response<ApiResponse<List<Warehouse>>>
    
    // ==================== 入库管理 ====================
    
    @POST("/api/inbound")
    suspend fun createInbound(@Body order: InboundOrderRequest): Response<ApiResponse<IdResponse>>
    
    @GET("/api/inbound")
    suspend fun getInboundList(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("status") status: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<ApiResponse<PagedResponse<InboundOrder>>>
    
    // ==================== 出库管理 ====================
    
    @POST("/api/outbound")
    suspend fun createOutbound(@Body order: OutboundOrderRequest): Response<ApiResponse<IdResponse>>
    
    @GET("/api/outbound")
    suspend fun getOutboundList(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<ApiResponse<PagedResponse<OutboundOrder>>>
    
    // ==================== 库存管理 ====================
    
    @GET("/api/inventory")
    suspend fun getInventory(
        @Query("warehouse_id") warehouseId: Int? = null,
        @Query("low_stock") lowStock: Boolean? = null
    ): Response<ApiResponse<List<InventoryItem>>>
    
    // ==================== 统计 ====================
    
    @GET("/api/stats")
    suspend fun getStats(): Response<ApiResponse<Stats>>
}
