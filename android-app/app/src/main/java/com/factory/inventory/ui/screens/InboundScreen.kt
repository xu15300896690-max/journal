package com.factory.inventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.factory.inventory.data.repository.InventoryRepository
import com.factory.inventory.data.supabase.InboundOrder
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboundScreen(
    onNavigateBack: () -> Unit = {}
) {
    var orders by remember { mutableStateOf<List<InboundOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            // TODO: 使用 InventoryRepository.getInboundList()
            orders = emptyList()
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("入库管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("暂无入库记录", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
                items(orders) { order ->
                    InboundOrderCard(order)
                }
            }
        }
    }
}

@Composable
fun InboundOrderCard(order: InboundOrder) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(order.order_no, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(order.status, color = if (order.status == "completed") Color.Green else Color.Orange, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("供应商：${order.supplier_name}", color = Color.Gray, fontSize = 14.sp)
            Text("仓库：${order.warehouse_name}", color = Color.Gray, fontSize = 14.sp)
            if (!order.plate_number.isNullOrBlank()) {
                Text("车牌：${order.plate_number}", color = Color.Gray, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("¥${String.format("%.2f", order.total_amount)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Blue)
                Text(order.created_at, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
