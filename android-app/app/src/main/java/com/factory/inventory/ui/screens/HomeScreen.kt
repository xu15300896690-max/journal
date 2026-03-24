package com.factory.inventory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.factory.inventory.data.MockData
import com.factory.inventory.data.api.ApiClient
import com.factory.inventory.data.model.InboundOrder
import com.factory.inventory.data.model.OutboundOrder
import com.factory.inventory.util.Config
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToInbound: () -> Unit = {},
    onNavigateToOutbound: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToBaseData: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var recentOrders by remember { mutableStateOf<List<RecentOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                if (Config.USE_LOCAL_DATA) {
                    // 使用本地测试数据
                    recentOrders = (MockData.mockInboundOrders + MockData.mockOutboundOrders.map {
                        RecentOrder(
                            orderNo = it.order_no,
                            type = "outbound",
                            partner = it.customer_name,
                            amount = it.total_amount,
                            date = it.created_at,
                            plateNumber = it.plate_number
                        )
                    }).map {
                        RecentOrder(
                            orderNo = it.orderNo,
                            type = it.type,
                            partner = it.partner,
                            amount = it.amount,
                            date = it.date,
                            plateNumber = it.plateNumber
                        )
                    }.sortedByDescending { it.date }.take(5)
                } else {
                    // 使用服务器数据
                    val inboundResponse = ApiClient.getService().getInboundList(page = 1, perPage = 5)
                    val inboundOrders = if (inboundResponse.isSuccessful) {
                        inboundResponse.body()?.data?.data?.map { order ->
                            RecentOrder(
                                orderNo = order.order_no,
                                type = "inbound",
                                partner = order.supplier_name,
                                amount = order.total_amount,
                                date = order.created_at,
                                plateNumber = order.plate_number
                            )
                        } ?: emptyList()
                    } else emptyList()
                    
                    val outboundResponse = ApiClient.getService().getOutboundList(page = 1, perPage = 5)
                    val outboundOrders = if (outboundResponse.isSuccessful) {
                        outboundResponse.body()?.data?.data?.map { order ->
                            RecentOrder(
                                orderNo = order.order_no,
                                type = "outbound",
                                partner = order.customer_name,
                                amount = order.total_amount,
                                date = order.created_at,
                                plateNumber = order.plate_number
                            )
                        } ?: emptyList()
                    } else emptyList()
                    
                    recentOrders = (inboundOrders + outboundOrders)
                        .sortedByDescending { it.date }
                        .take(5)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("工厂出入库管理") },
                actions = {
                    IconButton(onClick = onNavigateToBaseData) {
                        Icon(Icons.Default.Business, contentDescription = "基础数据")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "退出登录")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 功能按钮区域
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 入库按钮
                    ActionButton(
                        icon = Icons.Default.ArrowDownward,
                        title = "入库",
                        subtitle = "新建入库单",
                        backgroundColor = Color(0xFF1976D2),
                        onClick = onNavigateToInbound,
                        modifier = Modifier.weight(1f).height(100.dp)
                    )
                    
                    // 出库按钮
                    ActionButton(
                        icon = Icons.Default.ArrowUpward,
                        title = "出库",
                        subtitle = "新建出库单",
                        backgroundColor = Color(0xFFFF9800),
                        onClick = onNavigateToOutbound,
                        modifier = Modifier.weight(1f).height(100.dp)
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 库存查询
                    ActionButton(
                        icon = Icons.Default.Inventory,
                        title = "库存",
                        subtitle = "库存查询",
                        backgroundColor = Color(0xFF4CAF50),
                        onClick = onNavigateToInventory,
                        modifier = Modifier.weight(1f).height(100.dp)
                    )
                    
                    // 统计报表
                    ActionButton(
                        icon = Icons.Default.BarChart,
                        title = "统计",
                        subtitle = "报表统计",
                        backgroundColor = Color(0xFF9C27B0),
                        onClick = onNavigateToStats,
                        modifier = Modifier.weight(1f).height(100.dp)
                    )
                }
            }
            
            // 最近记录
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "最近记录",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 最近记录列表
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (recentOrders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂无记录", color = Color.Gray)
                    }
                }
            } else {
                items(recentOrders) { order ->
                    RecentOrderItem(order)
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

data class RecentOrder(
    val orderNo: String,
    val type: String,
    val partner: String,
    val amount: Double,
    val date: String,
    val plateNumber: String?
)

@Composable
fun RecentOrderItem(order: RecentOrder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (order.type == "inbound") Color(0xFF1976D2) else Color(0xFFFF9800),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.orderNo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = order.partner,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                if (!order.plateNumber.isNullOrBlank()) {
                    Text(
                        text = "🚗 ${order.plateNumber}",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (order.type == "inbound") "入库" else "出库",
                    color = if (order.type == "inbound") Color(0xFF1976D2) else Color(0xFFFF9800),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¥${String.format("%.2f", order.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (order.type == "inbound") Color(0xFF1976D2) else Color(0xFF4CAF50)
                )
                Text(
                    text = order.date.replace("T", " ").substring(0, 16),
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}
