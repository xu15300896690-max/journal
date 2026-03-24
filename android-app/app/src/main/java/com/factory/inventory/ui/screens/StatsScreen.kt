package com.factory.inventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.factory.inventory.data.api.ApiClient
import com.factory.inventory.data.model.Stats
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit = {}
) {
    var stats by remember { mutableStateOf<Stats?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = ApiClient.getService().getStats()
                if (response.isSuccessful) {
                    stats = response.body()?.data
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
                title = { Text("统计报表") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
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
                CircularProgressIndicator()
            }
        } else if (stats == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("加载失败", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 今日统计
                item {
                    Text("📊 今日统计", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "入库金额",
                            value = "¥${String.format("%.2f", stats!!.inbound_today)}",
                            color = Color(0xFF1976D2),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "出库金额",
                            value = "¥${String.format("%.2f", stats!!.outbound_today)}",
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    StatCard(
                        title = "今日利润",
                        value = "¥${String.format("%.2f", stats!!.profit_today)}",
                        color = if (stats!!.profit_today >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.fillMaxWidth(),
                        isProfit = true
                    )
                }
                
                // 本月统计
                item {
                    Text("📈 本月统计", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "入库金额",
                            value = "¥${String.format("%.2f", stats!!.inbound_month)}",
                            color = Color(0xFF1976D2),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "出库金额",
                            value = "¥${String.format("%.2f", stats!!.outbound_month)}",
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    StatCard(
                        title = "本月利润",
                        value = "¥${String.format("%.2f", stats!!.profit_month)}",
                        color = if (stats!!.profit_month >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.fillMaxWidth(),
                        isProfit = true
                    )
                }
                
                // 库存统计
                item {
                    Text("📦 库存统计", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "物品种类",
                            value = "${stats!!.total_items}",
                            color = Color(0xFF9C27B0),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "库存预警",
                            value = "${stats!!.low_stock_count}",
                            color = Color(0xFFF44336),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    isProfit: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = if (isProfit) 24.sp else 20.sp
            )
        }
    }
}
