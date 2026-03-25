package com.factory.inventory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.factory.inventory.data.repository.InventoryRepository
import com.factory.inventory.data.supabase.Stats
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
            // TODO: 使用 InventoryRepository 获取统计数据
            stats = Stats(
                inbound_today = 450000.00,
                outbound_today = 100000.00,
                profit_today = -350000.00,
                total_items = 8,
                low_stock_count = 2
            )
            isLoading = false
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
                },
                actions = {
                    IconButton(onClick = onNavigateToExport) {
                        Icon(Icons.Default.FileDownload, contentDescription = "导出")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (stats == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("加载失败", color = Color.Gray)
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("📊 今日统计", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("入库金额", "¥${String.format("%.2f", stats!!.inbound_today)}", listOf(Color(0xFF667eea), Color(0xFF764ba2)), Modifier.weight(1f))
                        StatCard("出库金额", "¥${String.format("%.2f", stats!!.outbound_today)}", listOf(Color(0xFF11998e), Color(0xFF38ef7d)), Modifier.weight(1f))
                    }
                }
                
                item {
                    StatCard(
                        "今日利润",
                        "¥${String.format("%.2f", stats!!.profit_today)}",
                        if (stats!!.profit_today >= 0) listOf(Color(0xFF11998e), Color(0xFF38ef7d)) else listOf(Color(0xFFeb3349), Color(0xFFf45c43)),
                        Modifier.fillMaxWidth(),
                        isProfit = true
                    )
                }
                
                item {
                    Text("📦 库存统计", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("物品种类", "${stats!!.total_items}", listOf(Color(0xFFDA22FF), Color(0xFF9733EE)), Modifier.weight(1f))
                        StatCard("库存预警", "${stats!!.low_stock_count}", listOf(Color(0xFFeb3349), Color(0xFFf45c43)), Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, gradientColors: List<Color>, modifier: Modifier = Modifier, isProfit: Boolean = false) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            Modifier.fillMaxSize().background(Brush.verticalGradient(colors = gradientColors)).padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = if (isProfit) 26.sp else 22.sp)
            }
        }
    }
}
