package com.factory.inventory.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.factory.inventory.data.api.ApiClient
import com.factory.inventory.data.model.InboundOrder
import com.factory.inventory.data.model.OutboundOrder
import com.factory.inventory.util.FileUtils
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 导出类型选择
            item {
                Text("📊 导出类型", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = selectedType == "inbound",
                        onClick = { selectedType = "inbound" },
                        label = { Text("入库单") },
                        leadingIcon = if (selectedType == "inbound") {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                    
                    FilterChip(
                        selected = selectedType == "outbound",
                        onClick = { selectedType = "outbound" },
                        label = { Text("出库单") },
                        leadingIcon = if (selectedType == "outbound") {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // 日期范围
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
            
            // 导出按钮
            item {
                Button(
                    onClick = {
                        isExporting = true
                        exportResult = null
                        
                        scope.launch {
                            try {
                                val fileName = exportData(
                                    context = context,
                                    type = selectedType,
                                    startDate = startDate,
                                    endDate = endDate
                                )
                                exportResult = if (fileName != null) {
                                    "✅ 导出成功：$fileName"
                                } else {
                                    "❌ 导出失败"
                                }
                            } catch (e: Exception) {
                                exportResult = "❌ 错误：${e.message}"
                            } finally {
                                isExporting = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isExporting
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("导出 Excel", fontSize = 16.sp)
                    }
                }
            }
            
            // 导出结果
            if (exportResult != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (exportResult!!.startsWith("✅")) {
                                Color(0xFFE8F5E9)
                            } else {
                                Color(0xFFFFEBEE)
                            }
                        )
                    ) {
                        Text(
                            text = exportResult!!,
                            modifier = Modifier.padding(16.dp),
                            color = if (exportResult!!.startsWith("✅")) {
                                Color(0xFF2E7D32)
                            } else {
                                Color(0xFFC62828)
                            }
                        )
                    }
                }
            }
            
            // 使用说明
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "💡 使用说明",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "1. 选择导出类型（入库单/出库单）",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "2. 选择日期范围（可选）",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "3. 点击导出按钮",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "4. 文件将保存到 下载/FactoryInventory 目录",
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/**
 * 导出数据到 Excel
 */
private suspend fun exportData(
    context: Context,
    type: String,
    startDate: String,
    endDate: String
): String? {
    return try {
        // 获取数据
        val orders = if (type == "inbound") {
            val response = ApiClient.getService().getInboundList(
                page = 1,
                perPage = 1000,
                startDate = startDate.ifBlank { null },
                endDate = endDate.ifBlank { null }
            )
            response.body()?.data?.data ?: emptyList()
        } else {
            val response = ApiClient.getService().getOutboundList(page = 1, perPage = 1000)
            response.body()?.data?.data ?: emptyList()
        }
        
        // 生成 Excel 内容（CSV 格式）
        val csvContent = buildString {
            // 表头
            if (type == "inbound") {
                appendLine("单号，供应商，仓库，车牌，司机，数量，金额，日期")
            } else {
                appendLine("单号，客户，仓库，车牌，数量，金额，日期")
            }
            
            // 数据行
            orders.forEach { order ->
                if (type == "inbound") {
                    val inbound = order as InboundOrder
                    appendLine(
                        "${inbound.order_no}," +
                        "${inbound.supplier_name}," +
                        "${inbound.warehouse_name}," +
                        "${inbound.plate_number.orEmpty()}," +
                        "${inbound.driver_name.orEmpty()}," +
                        "${inbound.total_amount}," +
                        "${inbound.total_amount}," +
                        "${inbound.created_at.replace("T", " ").substring(0, 10)}"
                    )
                } else {
                    val outbound = order as OutboundOrder
                    appendLine(
                        "${outbound.order_no}," +
                        "${outbound.customer_name}," +
                        "${outbound.warehouse_name}," +
                        "${outbound.plate_number.orEmpty()}," +
                        "${outbound.total_amount}," +
                        "${outbound.total_amount}," +
                        "${outbound.created_at.replace("T", " ").substring(0, 10)}"
                    )
                }
            }
        }
        
        // 生成文件名
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Factory_${type}_${timeStamp}.csv"
        
        // 保存文件
        val file = FileUtils.exportExcel(context, fileName, csvContent)
        
        // 分享文件
        file?.let {
            shareFile(context, it)
        }
        
        fileName
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 分享文件
 */
private fun shareFile(context: Context, file: File) {
    try {
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "打开文件"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
