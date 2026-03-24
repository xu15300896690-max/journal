package com.factory.inventory.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.factory.inventory.data.api.ApiClient
import com.factory.inventory.data.model.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboundScreen(
    onNavigateBack: () -> Unit = {}
) {
    var showForm by remember { mutableStateOf(false) }
    var orders by remember { mutableStateOf<List<InboundOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    // 加载入库单列表
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = ApiClient.getService().getInboundList(page = 1, perPage = 50)
                if (response.isSuccessful) {
                    orders = response.body()?.data?.data ?: emptyList()
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
                title = { Text("入库管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showForm = true }) {
                        Icon(Icons.Default.Add, contentDescription = "新建入库")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showForm = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "新建入库")
            }
        }
    ) { paddingValues ->
        if (showForm) {
            InboundFormScreen(
                onSubmitted = {
                    showForm = false
                    // 重新加载列表
                    scope.launch {
                        val response = ApiClient.getService().getInboundList(page = 1, perPage = 50)
                        if (response.isSuccessful) {
                            orders = response.body()?.data?.data ?: emptyList()
                        }
                    }
                },
                onNavigateBack = { showForm = false }
            )
        } else if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (orders.isEmpty()) {
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
                    Text("暂无入库记录", color = Color.Gray)
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
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 第一行：单号 + 状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = order.order_no,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                StatusBadge(order.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 第二行：供应商 + 仓库
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📦 ${order.supplier_name}",
                    color = Color.Gray
                )
                Text(
                    text = "🏭 ${order.warehouse_name}",
                    color = Color.Gray
                )
            }
            
            // 第三行：车牌 + 司机
            if (!order.plate_number.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🚗 ${order.plate_number}  ${order.driver_name ?: ""}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 第四行：金额 + 时间
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "¥${String.format("%.2f", order.total_amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = order.created_at.replace("T", " ").substring(0, 16),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        "completed" -> Color(0xFF4CAF50) to "已完成"
        "pending" -> Color(0xFFFF9800) to "待处理"
        "cancelled" -> Color(0xFFF44336) to "已取消"
        else -> Color.Gray to status
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// ==================== 入库表单 ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboundFormScreen(
    onSubmitted: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var warehouseId by remember { mutableStateOf(1) }
    var supplierId by remember { mutableStateOf("") }
    var plateNumber by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var driverPhone by remember { mutableStateOf("") }
    
    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("件") }
    var unitPrice by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var weightUnit by remember { mutableStateOf("吨") }
    var note by remember { mutableStateOf("") }
    
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    // 模拟供应商列表
    val suppliers = listOf(
        Supplier(1, "上海钢铁厂", "张三", "13800138000", "上海市"),
        Supplier(2, "五金建材公司", "李四", "13900139000", "苏州市"),
        Supplier(3, "原材料供应商", "王五", "13700137000", "杭州市")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新建入库单") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "取消")
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
            // 车辆信息
            item {
                Text("🚗 车辆信息", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            item {
                OutlinedTextField(
                    value = plateNumber,
                    onValueChange = { plateNumber = it },
                    label = { Text("车牌号") },
                    placeholder = { Text("如：沪 A12345") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            item {
                OutlinedTextField(
                    value = driverName,
                    onValueChange = { driverName = it },
                    label = { Text("司机姓名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            item {
                OutlinedTextField(
                    value = driverPhone,
                    onValueChange = { driverPhone = it },
                    label = { Text("司机电话") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }
            
            // 物品信息
            item {
                Text("📦 物品信息", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            item {
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {
                    OutlinedTextField(
                        value = suppliers.find { it.id.toString() == supplierId }?.name ?: "选择供应商",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("供应商") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                    )
                }
            }
            
            item {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("物品名称") },
                    placeholder = { Text("如：钢材、螺丝") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("数量") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = {}
                    ) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("单位") },
                            modifier = Modifier
                                .width(100.dp)
                                .menuAnchor(),
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                        )
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("重量") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = {}
                    ) {
                        OutlinedTextField(
                            value = weightUnit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("单位") },
                            modifier = Modifier
                                .width(80.dp)
                                .menuAnchor(),
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                        )
                    }
                }
            }
            
            item {
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = { unitPrice = it },
                    label = { Text("单价 (元)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
            
            // 备注
            item {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
            
            // 错误提示
            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
            
            // 提交按钮
            item {
                Button(
                    onClick = {
                        if (itemName.isBlank() || quantity.isBlank() || unitPrice.isBlank()) {
                            errorMessage = "请填写必填项"
                            return@Button
                        }
                        
                        isSubmitting = true
                        errorMessage = null
                        
                        scope.launch {
                            try {
                                val response = ApiClient.getService().createInbound(
                                    InboundOrderRequest(
                                        warehouse_id = warehouseId,
                                        supplier_id = supplierId.toIntOrNull() ?: 1,
                                        plate_number = plateNumber,
                                        driver_name = driverName,
                                        driver_phone = driverPhone,
                                        items = listOf(
                                            InboundItemRequest(
                                                item_id = 1,
                                                quantity = quantity.toIntOrNull() ?: 0,
                                                unit = unit,
                                                net_weight = weight.toDoubleOrNull(),
                                                weight_unit = weightUnit,
                                                unit_price = unitPrice.toDoubleOrNull() ?: 0.0
                                            )
                                        ),
                                        note = note
                                    )
                                )
                                
                                if (response.isSuccessful && response.body()?.success == true) {
                                    onSubmitted()
                                } else {
                                    errorMessage = response.body()?.message ?: "提交失败"
                                }
                            } catch (e: Exception) {
                                errorMessage = "网络错误：${e.message}"
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("确认入库", fontSize = 16.sp)
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
