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
import com.factory.inventory.data.repository.BaseDataRepository
import com.factory.inventory.data.supabase.Customer
import com.factory.inventory.data.supabase.Item
import com.factory.inventory.data.supabase.Supplier
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseDataScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("供应商", "客户", "物品")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("基础数据") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            when (selectedTab) {
                0 -> SupplierList()
                1 -> CustomerList()
                2 -> ItemList()
            }
        }
    }
}

@Composable
fun SupplierList() {
    var suppliers by remember { mutableStateOf<List<Supplier>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            // TODO: 使用 BaseDataRepository.getSuppliers()
            suppliers = emptyList()
            isLoading = false
        }
    }
    
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (suppliers.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("暂无供应商", color = Color.Gray)
        }
    } else {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            items(suppliers) { supplier ->
                SupplierCard(supplier)
            }
        }
    }
}

@Composable
fun SupplierCard(supplier: Supplier) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(supplier.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("联系人：${supplier.contact ?: "-"}", color = Color.Gray, fontSize = 14.sp)
            Text("电话：${supplier.phone ?: "-"}", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun CustomerList() {
    var customers by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            // TODO: 使用 BaseDataRepository.getCustomers()
            customers = emptyList()
            isLoading = false
        }
    }
    
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (customers.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("暂无客户", color = Color.Gray)
        }
    } else {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            items(customers) { customer ->
                CustomerCard(customer)
            }
        }
    }
}

@Composable
fun CustomerCard(customer: Customer) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(customer.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("联系人：${customer.contact ?: "-"}", color = Color.Gray, fontSize = 14.sp)
            Text("电话：${customer.phone ?: "-"}", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun ItemList() {
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            // TODO: 使用 BaseDataRepository.getItems()
            items = emptyList()
            isLoading = false
        }
    }
    
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (items.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("暂无物品", color = Color.Gray)
        }
    } else {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            items(items) { item ->
                ItemCard(item)
            }
        }
    }
}

@Composable
fun ItemCard(item: Item) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("编码：${item.code ?: "-"}", color = Color.Gray, fontSize = 14.sp)
            Text("规格：${item.spec ?: "-"}", color = Color.Gray, fontSize = 14.sp)
            Text("最低库存：${item.min_stock} ${item.unit}", color = Color.Gray, fontSize = 14.sp)
        }
    }
}
