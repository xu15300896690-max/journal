package com.factory.inventory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.factory.inventory.data.MockData
import com.factory.inventory.data.api.ApiClient
import com.factory.inventory.ui.theme.*
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
                    val inboundOrders = MockData.mockInboundOrders.map {
                        RecentOrder(
                            orderNo = it.order_no,
                            type = "inbound",
                            partner = it.supplier_name,
                            amount = it.total_amount,
                            date = it.created_at,
                            plateNumber = it.plate_number,
                            status = "进行中",
                            statusColor = StatusProcessing
                        )
                    }
                    val outboundOrders = MockData.mockOutboundOrders.map {
                        RecentOrder(
                            orderNo = it.order_no,
                            type = "outbound",
                            partner = it.customer_name,
                            amount = it.total_amount,
                            date = it.created_at,
                            plateNumber = it.plate_number,
                            status = "已完成",
                            statusColor = StatusSuccess
                        )
                    }
                    recentOrders = (inboundOrders + outboundOrders)
                        .sortedByDescending { it.date }
                        .take(5)
                } else {
                    val inboundResponse = ApiClient.getService().getInboundList(page = 1, perPage = 5)
                    val inboundOrders = if (inboundResponse.isSuccessful) {
                        inboundResponse.body()?.data?.data?.map { order ->
                            RecentOrder(
                                orderNo = order.order_no,
                                type = "inbound",
                                partner = order.supplier_name,
                                amount = order.total_amount,
                                date = order.created_at,
                                plateNumber = order.plate_number,
                                status = "进行中",
                                statusColor = StatusProcessing
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
                                plateNumber = order.plate_number,
                                status = "已完成",
                                statusColor = StatusSuccess
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
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 顶部栏 - 品牌 + 操作员
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 品牌标识
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color(0xFF000000),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "xurui",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF000000)
                            )
                            Text(
                                text = "Clean Energy",
                                fontSize = 13.sp,
                                color = Color(0xFF666666),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    // 操作员信息 + 头像
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "操作员 042",
                            color = Color(0xFF666666),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF999999),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // 功能模块 - 2x2 网格
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 入库作业
                    FunctionCard(
                        icon = Icons.Default.Login,
                        title = "入库作业",
                        backgroundColor = Color(0xFFFFF0F0),
                        iconColor = Color(0xFFD32F2F),
                        onClick = onNavigateToInbound,
                        modifier = Modifier.weight(1f).height(140.dp)
                    )
                    
                    // 出库拣货
                    FunctionCard(
                        icon = Icons.Default.Logout,
                        title = "出库拣货",
                        backgroundColor = Color(0xFFF0F4FF),
                        iconColor = Color(0xFF1976D2),
                        onClick = onNavigateToOutbound,
                        modifier = Modifier.weight(1f).height(140.dp)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 查询统计
                    FunctionCard(
                        icon = Icons.Default.TrendingUp,
                        title = "查询统计",
                        backgroundColor = Color(0xFFF0FFF4),
                        iconColor = Color(0xFF388E3C),
                        onClick = onNavigateToStats,
                        modifier = Modifier.weight(1f).height(140.dp)
                    )
                    
                    // 基础数据
                    FunctionCard(
                        icon = Icons.Default.Dns,
                        title = "基础数据",
                        backgroundColor = Color(0xFFF5F0FF),
                        iconColor = Color(0xFF7B1FA2),
                        onClick = onNavigateToBaseData,
                        modifier = Modifier.weight(1f).height(140.dp)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // 最近记录标题
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "最近记录",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    TextButton(onClick = { }) {
                        Text(
                            text = "查看全部",
                            color = Color(0xFF999999),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(12.dp))
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
                        CircularProgressIndicator(
                            color = EnergyBlue
                        )
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
                        Text(
                            text = "暂无记录",
                            color = Color(0xFF999999),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(recentOrders) { order ->
                    RecentOrderItem(order)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FunctionCard(
    icon: ImageVector,
    title: String,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = iconColor
                )
            }
        }
    }
}

@Composable
fun RecentOrderItem(order: RecentOrder) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧图标背景
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (order.type == "inbound") 
                                Color(0xFFF0F4FF) 
                            else 
                                Color(0xFFF0FFF4)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (order.type == "inbound") 
                            Icons.Default.Login 
                        else 
                            Icons.Default.Logout,
                        contentDescription = null,
                        tint = if (order.type == "inbound") 
                            Color(0xFF1976D2) 
                        else 
                            Color(0xFF388E3C),
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // 中间内容
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (order.type == "inbound") "入库：${order.orderNo}" else "出库：${order.orderNo}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF333333)
                        )
                        
                        // 状态标签
                        Surface(
                            color = order.statusColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = order.status,
                                color = order.statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (order.type == "inbound") 
                            "供应商：${order.partner}" 
                        else 
                            "装载区域：${order.partner}",
                        fontSize = 13.sp,
                        color = Color(0xFF999999)
                    )
                    if (!order.plateNumber.isNullOrBlank()) {
                        Text(
                            text = "库位：${order.plateNumber}",
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }
            
            // 右侧时间
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = order.date.replace("T", " ").substring(11, 16),
                    color = Color(0xFF999999),
                    fontSize = 13.sp
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFFCCCCCC),
                    modifier = Modifier.size(20.dp)
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
    val plateNumber: String?,
    val status: String,
    val statusColor: Color
)
