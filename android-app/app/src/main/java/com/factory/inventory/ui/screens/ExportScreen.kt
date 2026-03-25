package com.factory.inventory.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.factory.inventory.data.api.ApiClient
import com.factory.inventory.data.model.InboundOrder
import com.factory.inventory.data.model.OutboundOrder
import com.factory.inventory.ui.theme.*
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
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text("数据导出")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EnergyBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 导出类型选择
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Category,
                            contentDescription = null,
                            tint = EnergyBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text("导出类型", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = selectedType == "inbound",
                        onClick = { selectedType = "inbound" },
                        label = { 
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("入库单")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    FilterChip(
                        selected = selectedType == "outbound",
                        onClick = { selectedType = "outbound" },
                        label = { 
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("出库单")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            
            // 日期范围
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = InventoryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text("日期范围", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                }
            }
            
            item {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("开始日期") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Event, contentDescription = null, tint = EnergyBlue)
                    },
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
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Event, contentDescription = null, tint = EnergyBlue)
                    },
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
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isExporting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EnergyBlue
                    )
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text("导出 Excel", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
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
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (exportResult!!.startsWith("✅")) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (exportResult!!.startsWith("✅")) StatusSuccess else StatusError,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = exportResult!!,
                                color = if (exportResult!!.startsWith("✅")) StatusSuccess else StatusError,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            // 使用说明
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = EnergyBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "使用说明",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• 选择导出类型（入库单/出库单）",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = "• 选择日期范围（可选）",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = "• 点击导出按钮",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = "• 文件将保存到 下载/FactoryInventory 目录",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private suspend fun exportData(
    context: Context,
    type: String,
    startDate: String,
    endDate: String
): String? {
    return try {
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
        
        val csvContent = buildString {
            if (type == "inbound") {
                appendLine("单号，供应商，仓库，车牌，司机，数量，金额，日期")
            } else {
                appendLine("单号，客户，仓库，车牌，数量，金额，日期")
            }
            
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
        
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Factory_${type}_${timeStamp}.csv"
        
        val file = FileUtils.exportExcel(context, fileName, csvContent)
        
        file?.let {
            shareFile(context, it)
        }
        
        fileName
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

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
