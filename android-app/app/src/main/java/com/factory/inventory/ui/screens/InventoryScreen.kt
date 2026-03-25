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
import com.factory.inventory.data.supabase.Inventory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit = {}
) {
    var inventoryList by remember { mutableStateOf<List<Inventory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showLowStockOnly by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(showLowStockOnly) {
        scope.launch {
            // TODO: 使用 InventoryRepository.getInventory()
            inventoryList = emptyList()
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("库存查询") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    FilterChip(
                        selected = showLowStockOnly,
                        onClick = { showLowStockOnly = !showLowStockOnly },
                        label = { Text("仅看预警") }
                    )
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (inventoryList.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("暂无库存记录", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
                items(inventoryList) { item ->
                    InventoryCard(item)
                }
            }
        }
    }
}

@Composable
fun InventoryCard(item: Inventory) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = if (item.is_low) CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) else CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.item_name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (item.is_low) {
                    Surface(color = Color.Red.copy(alpha = 0.1f), shape = MaterialTheme.shapes.small) {
                        Text("⚠️ 预警", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("编码：${item.item_code ?: "-"}", color = Color.Gray, fontSize = 14.sp)
            Text("仓库：${item.warehouse_name}", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${item.quantity} ${item.unit}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = if (item.is_low) Color.Red else Color.Green)
                Text("最低：${item.min_stock} ${item.unit}", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}
