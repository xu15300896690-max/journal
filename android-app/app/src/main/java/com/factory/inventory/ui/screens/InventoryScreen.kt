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
import com.factory.inventory.data.MockData
import com.factory.inventory.data.api.ApiClient
import com.factory.inventory.data.model.InventoryItem
import com.factory.inventory.util.Config
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit = {}
) {
    var inventoryList by remember { mutableStateOf<List<InventoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showLowStockOnly by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    // 加载库存列表
    LaunchedEffect(showLowStockOnly) {
        scope.launch {
            try {
                if (Config.USE_LOCAL_DATA) {
                    inventoryList = if (showLowStockOnly) {
                        MockData.mockInventory.filter { it.is_low }
                    } else {
                        MockData.mockInventory
                    }
                } else {
                    val response = ApiClient.getService().getInventory(
                        lowStock = if (showLowStockOnly) true else null
                    )
                    if (response.isSuccessful) {
                        inventoryList = response.body()?.data ?: emptyList()
                    }
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
                        label = { Text("仅看预警") },
                        leadingIcon = if (showLowStockOnly) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
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
        } else if (inventoryList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (showLowStockOnly) "暂无预警库存" else "暂无库存记录",
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(inventoryList) { item ->
                    InventoryCard(item)
                }
            }
        }
    }
}

@Composable
fun InventoryCard(item: InventoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (item.is_low) {
            CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 第一行：物品名称 + 仓库
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.item_name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                
                if (item.is_low) {
                    Surface(
                        color = Color(0xFFF44336).copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "⚠️ 预警",
                            color = Color(0xFFF44336),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 第二行：物品编码
            if (!item.item_code.isNullOrBlank()) {
                Text(
                    text = "编码：${item.item_code}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // 第三行：库存信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "🏭 ${item.warehouse_name}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                
                Row {
                    Text(
                        text = "📦 ${item.quantity} ${item.unit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (item.is_low) Color(0xFFF44336) else Color(0xFF4CAF50)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 第四行：最低库存
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "最低库存：${item.min_stock} ${item.unit}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                
                if (item.is_low) {
                    Text(
                        text = "缺货 ${item.min_stock - item.quantity} ${item.unit}",
                        color = Color(0xFFF44336),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
