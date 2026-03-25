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
import androidx.compose.ui.graphics.Brush
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
                            plateNumber = it.plate_number
                        )
                    }
                    val outboundOrders = MockData.mockOutboundOrders.map {
                        RecentOrder(
                            orderNo = it.order_no,
                            type = "outbound",
                            partner = it.customer_name,
                            amount = it.total_amount,
                            date = it.created_at,
                            plateNumber = it.plate_number
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
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundGray
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 顶部栏 - 品牌 + 操作员
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 品牌标识
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = EnergyCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "xurui",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Clean Energy",
                                fontSize = 12.sp,
                                color = EnergyBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    // 操作员信息
                    Row(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(EnergyCyan, EnergyBlue)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = EnergyBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "操作员 042",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // 入库作业主卡片
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF424242), Color(0xFF212121))
                                )
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 状态指示
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(StatusSuccess),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // 脉冲动画效果可以后续添加
                                }
                                Text(
                                    text = "READY TO SCAN",
                                    color = StatusSuccess,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )
                            }
                            
                            // 标题
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "入库作业",
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "点击开始处理新的 PO 入库申请",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            
                            // 开始扫描按钮
                            Button(
                                onClick = onNavigateToInbound,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color(0xFF212121)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "开始扫描",
                                    color = Color(0xFF212121),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            
            // 功能模块 - 3 列
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 出库拣货
                    FunctionCard(
                        icon = Icons.Default.ArrowUpward,
                        title = "出库拣货",
                        backgroundColor = Color(0xFFE3F2FD),
                        iconColor = Color(0xFF1976D2),
                        onClick = onNavigateToOutbound,
                        modifier = Modifier.weight(1f).height(120.dp)
                    )
                    
                    // 查询统计
                    FunctionCard(
                        icon = Icons.Default.BarChart,
                        title = "查询统计",
                        backgroundColor = Color(0xFFE8F5E9),
                        iconColor = Color(0xFF4CAF50),
                        onClick = onNavigateToStats,
                        modifier = Modifier.weight(1f).height(120.dp)
                    )
                    
                    // 基础数据
                    FunctionCard(
                        icon = Icons.Default.Business,
                        title = "基础数据",
                        backgroundColor = Color(0xFFF3E5F5),
                        iconColor = Color(0xFF9C27B0),
                        onClick = onNavigateToBaseData,
                        modifier = Modifier.weight(1f).height(120.dp)
                    )
                }
            }
            
            // 最近记录
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardWhite
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "最近记录",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            TextButton(onClick = { }) {
                                Text(
                                    text = "查看全部",
                                    color = EnergyBlue,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // 记录列表
                        if (isLoading) {
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
                        } else if (recentOrders.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "暂无记录",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            recentOrders.take(3).forEach { order ->
                                RecentOrderItem(order)
                                if (order != recentOrders.last()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun RecentOrderItem(order: RecentOrder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (order.type == "inbound") 
                                Color(0xFFE3F2FD) 
                            else 
                                Color(0xFFE8F5E9)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (order.type == "inbound") 
                            Icons.Default.ArrowDownward 
                        else 
                            Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (order.type == "inbound") 
                            Color(0xFF1976D2) 
                        else 
                            Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Column {
                    Text(
                        text = order.orderNo,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = order.partner,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    if (!order.plateNumber.isNullOrBlank()) {
                        Text(
                            text = "🚗 ${order.plateNumber}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (order.type == "inbound") "入库" else "出库",
                    color = if (order.type == "inbound") 
                        Color(0xFF1976D2) 
                    else 
                        Color(0xFF4CAF50),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Text(
                    text = "¥${String.format("%.2f", order.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (order.type == "inbound") 
                        Color(0xFF1976D2) 
                    else 
                        Color(0xFF4CAF50)
                )
                Text(
                    text = order.date.replace("T", " ").substring(0, 16),
                    color = TextSecondary,
                    fontSize = 10.sp
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
