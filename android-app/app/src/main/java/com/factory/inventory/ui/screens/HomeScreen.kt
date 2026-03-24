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
import com.factory.inventory.data.api.ApiClient
import com.factory.inventory.data.model.InboundOrder
import com.factory.inventory.data.model.OutboundOrder
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToInbound: () -> Unit = {},
    onNavigateToOutbound: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var recentOrders by remember { mutableStateOf<List<RecentOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    // 加载最近记录
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // 获取最近入库
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
                
                // 获取最近出库
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
                
                // 合并并排序
                recentOrders = (inboundOrders + outboundOrders)
                    .sortedByDescending { it.date }
                    .take(5)
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
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "退出登录")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 入库按钮
            ActionButton(
                icon = Icons.Default.ArrowDownward,
                title = "入库管理",
                subtitle = "新建入库单 / 入库记录",
                backgroundColor = Color(0xFF1976D2),
                onClick = onNavigateToInbound,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 出库按钮
            ActionButton(
                icon = Icons.Default.ArrowUpward,
                title = "出库管理",
                subtitle = "新建出库单 / 出库记录",
                backgroundColor = Color(0xFFFF9800),
                onClick = onNavigateToOutbound,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 最近记录标题
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
                Text(
                    text = "查看更多 >",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 最近记录列表
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (recentOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无记录", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentOrders) { order ->
                        RecentOrderItem(order)
                    }
                }
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
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitle,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

data class RecentOrder(
    val orderNo: String,
    val type: String, // "inbound" or "outbound"
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
                    // 类型标识
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
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = order.partner,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                
                if (!order.plateNumber.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "🚗 ${order.plateNumber}",
                        color = Color.Gray,
                        fontSize = 12.sp
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
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "¥${String.format("%.2f", order.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (order.type == "inbound") Color(0xFF1976D2) else Color(0xFF4CAF50)
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = order.date.replace("T", " ").substring(0, 16),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
