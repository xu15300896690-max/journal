package com.factory.inventory.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.File

/**
 * 扫码枪/扫码组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScanner(
    onBarcodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }
    var scannedResult by remember { mutableStateOf<String?>(null) }
    
    // 模拟扫码（实际项目中需要使用 CameraX + ML Kit）
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.QrCodeScanner,
                contentDescription = "扫码",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = scannedResult ?: "点击扫描条码/二维码",
                fontSize = 14.sp,
                color = if (scannedResult != null) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        // TODO: 实现真实扫码功能
                        // 这里模拟扫码结果
                        scannedResult = "SCAN${System.currentTimeMillis()}"
                        onBarcodeScanned(scannedResult!!)
                        isScanning = false
                    },
                    enabled = !isScanning
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = "扫码",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("扫码")
                }
                
                if (scannedResult != null) {
                    OutlinedButton(
                        onClick = {
                            scannedResult = null
                            onBarcodeScanned("")
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "重扫",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("重扫")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "💡 提示：支持一维码、二维码",
                fontSize = 12.sp,
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }
    }
}

/**
 * 扫码输入组件（带扫码枪支持）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "条码/二维码",
    modifier: Modifier = Modifier
) {
    var showScanner by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                trailingIcon = {
                    if (value.isNotEmpty()) {
                        IconButton(onClick = { onValueChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "清空")
                        }
                    }
                }
            )
            
            IconButton(
                onClick = { showScanner = true },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "扫码")
            }
        }
        
        // 扫码对话框
        if (showScanner) {
            AlertDialog(
                onDismissRequest = { showScanner = false },
                title = { Text("扫码") },
                text = {
                    BarcodeScanner(
                        onBarcodeScanned = { code ->
                            onValueChange(code)
                            showScanner = false
                        }
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showScanner = false }) {
                        Text("关闭")
                    }
                }
            )
        }
    }
}
