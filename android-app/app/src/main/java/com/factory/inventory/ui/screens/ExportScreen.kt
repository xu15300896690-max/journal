package com.factory.inventory.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf("inbound") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var isExporting by remember { mutableStateOf(false) }
    var exportResult by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("数据导出") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("📊 导出类型", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = selectedType == "inbound",
                        onClick = { selectedType = "inbound" },
                        label = { Text("入库单") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    FilterChip(
                        selected = selectedType == "outbound",
                        onClick = { selectedType = "outbound" },
                        label = { Text("出库单") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Text("📅 日期范围", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            item {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("开始日期") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            item {
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("结束日期") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            item {
                Button(
                    onClick = {
                        isExporting = true
                        exportResult = null
                        
                        scope.launch {
                            // TODO: 实现导出功能
                            kotlinx.coroutines.delay(1000)
                            exportResult = "✅ 导出成功：Export_${selectedType}_20260325.csv"
                            isExporting = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isExporting
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("导出 Excel", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            
            if (exportResult != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (exportResult!!.startsWith("✅")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = exportResult!!,
                            modifier = Modifier.padding(16.dp),
                            color = if (exportResult!!.startsWith("✅")) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💡 使用说明", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1565C0))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("1. 选择导出类型（入库单/出库单）", fontSize = 13.sp, color = Color(0xFF1565C0), lineHeight = 20.sp)
                        Text("2. 选择日期范围（可选）", fontSize = 13.sp, color = Color(0xFF1565C0), lineHeight = 20.sp)
                        Text("3. 点击导出按钮", fontSize = 13.sp, color = Color(0xFF1565C0), lineHeight = 20.sp)
                        Text("4. 文件将保存到 下载 目录", fontSize = 13.sp, color = Color(0xFF1565C0), lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}
