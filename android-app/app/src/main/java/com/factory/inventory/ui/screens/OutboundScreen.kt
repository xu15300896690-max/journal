package com.factory.inventory.ui.screens

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
import com.factory.inventory.data.MockData
import com.factory.inventory.data.api.ApiClient
import com.factory.inventory.data.model.*
import com.factory.inventory.util.Config
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutboundScreen(
    onNavigateBack: () -> Unit = {}
) {
    var showForm by remember { mutableStateOf(false) }
    var orders by remember { mutableStateOf<List<OutboundOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                if (Config.USE_LOCAL_DATA) {
                    orders = MockData.mockOutboundOrders
                } else {
                    val response = ApiClient.getService().getOutboundList(page = 1, perPage = 50)
                    if (response.isSuccessful) {
                        orders = response.body()?.data?.data ?: emptyList()
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
                title = { Text("出库管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showForm = true }) {
                        Icon(Icons.Default.Add, contentDescription = "新建出库")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showForm = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "新建出库")
            }
        }
    ) { paddingValues ->
        if (showForm) {
            OutboundFormScreen(
                onSubmitted = {
                    showForm = false
                    scope.launch {
                        val response = ApiClient.getService().getOutboundList(page = 1, perPage = 50)
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
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("暂无出库记录", color = Color.Gray)
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
                    OutboundOrderCard(order)
                }
            }
        }
    }
}

@Composable
fun OutboundOrderCard(order: OutboundOrder) {
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
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "👤 ${order.customer_name}",
                    color = Color.Gray
                )
                Text(
                    text = "🏭 ${order.warehouse_name}",
                    color = Color.Gray
                )
            }
            
            if (!order.plate_number.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🚗 ${order.plate_number}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "¥${String.format("%.2f", order.total_amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF4CAF50)
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

// ==================== 出库表单 ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutboundFormScreen(
    onSubmitted: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var warehouseId by remember { mutableStateOf(1) }
    var customerId by remember { mutableStateOf("") }
    var plateNumber by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var driverPhone by remember { mutableStateOf("") }
    
    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("件") }
    var unitPrice by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    val customers = listOf(
        Customer(1, "某建筑公司", "赵六", "13600136000", "南京市"),
        Customer(2, "贸易公司", "钱七", "13500135000", "武汉市"),
        Customer(3, "制造企业", "孙八", "13400134000", "成都市")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新建出库单") },
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
            item {
                Text("👤 客户信息", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            item {
                OutlinedTextField(
                    value = customers.find { it.id.toString() == customerId }?.name ?: "选择客户",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("客户") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                )
            }
            
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
            
            item {
                Text("📦 物品信息", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = { unitPrice = it },
                    label = { Text("单价 (元)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
            
            item {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
            
            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
            
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
                                val response = ApiClient.getService().createOutbound(
                                    OutboundOrderRequest(
                                        warehouse_id = warehouseId,
                                        customer_id = customerId.toIntOrNull() ?: 1,
                                        plate_number = plateNumber,
                                        driver_name = driverName,
                                        driver_phone = driverPhone,
                                        items = listOf(
                                            OutboundItemRequest(
                                                item_id = 1,
                                                quantity = quantity.toIntOrNull() ?: 0,
                                                unit = unit,
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
                        Text("确认出库", fontSize = 16.sp)
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
