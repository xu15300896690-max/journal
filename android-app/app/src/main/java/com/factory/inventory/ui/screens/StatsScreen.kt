package com.factory.inventory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.factory.inventory.data.model.Stats
import com.factory.inventory.ui.theme.*
import com.factory.inventory.util.Config
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToExport: () -> Unit = {}
) {
    var stats by remember { mutableStateOf<Stats?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                if (Config.USE_LOCAL_DATA) {
                    stats = MockData.mockStats
                    isLoading = false
                } else {
                    val response = ApiClient.getService().getStats()
                    if (response.isSuccessful) {
                        stats = response.body()?.data
                    }
                    isLoading = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text("统计报表")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StatsPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onNavigateToExport) {
                        Icon(Icons.Default.FileDownload, contentDescription = "导出")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = StatsPurple)
            }
        } else if (stats == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("加载失败", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // 今日统计
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF3E5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Today,
                                    contentDescription = null,
                                    tint = StatsPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text("今日统计", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        }
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = Icons.Default.ArrowDownward,
                            title = "入库金额",
                            value = "¥${String.format("%.2f", stats!!.inbound_today)}",
                            gradientColors = listOf(Color(0xFF667eea), Color(0xFF764ba2)),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.ArrowUpward,
                            title = "出库金额",
                            value = "¥${String.format("%.2f", stats!!.outbound_today)}",
                            gradientColors = listOf(Color(0xFF11998e), Color(0xFF38ef7d)),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    StatCard(
                        icon = Icons.Default.TrendingUp,
                        title = "今日利润",
                        value = "¥${String.format("%.2f", stats!!.profit_today)}",
                        gradientColors = if (stats!!.profit_today >= 0) {
                            listOf(Color(0xFF11998e), Color(0xFF38ef7d))
                        } else {
                            listOf(Color(0xFFeb3349), Color(0xFFf45c43))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isProfit = true
                    )
                }
                
                // 本月统计
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE3F2FD)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = InboundBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text("本月统计", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        }
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = Icons.Default.ArrowDownward,
                            title = "入库金额",
                            value = "¥${String.format("%.2f", stats!!.inbound_month)}",
                            gradientColors = listOf(Color(0xFF667eea), Color(0xFF764ba2)),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.ArrowUpward,
                            title = "出库金额",
                            value = "¥${String.format("%.2f", stats!!.outbound_month)}",
                            gradientColors = listOf(Color(0xFF11998e), Color(0xFF38ef7d)),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    StatCard(
                        icon = Icons.Default.TrendingUp,
                        title = "本月利润",
                        value = "¥${String.format("%.2f", stats!!.profit_month)}",
                        gradientColors = if (stats!!.profit_month >= 0) {
                            listOf(Color(0xFF11998e), Color(0xFF38ef7d))
                        } else {
                            listOf(Color(0xFFeb3349), Color(0xFFf45c43))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isProfit = true
                    )
                }
                
                // 库存统计
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Inventory,
                                    contentDescription = null,
                                    tint = OutboundOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text("库存统计", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        }
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = Icons.Default.Category,
                            title = "物品种类",
                            value = "${stats!!.total_items}",
                            gradientColors = listOf(Color(0xFFDA22FF), Color(0xFF9733EE)),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Default.Warning,
                            title = "库存预警",
                            value = "${stats!!.low_stock_count}",
                            gradientColors = listOf(Color(0xFFeb3349), Color(0xFFf45c43)),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    isProfit: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = title,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isProfit) 26.sp else 22.sp
                )
            }
        }
    }
}
