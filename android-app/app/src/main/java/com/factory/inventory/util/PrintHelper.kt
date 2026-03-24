package com.factory.inventory.util

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * 打印工具
 */
class PrintHelper(private val context: Context) {
    
    /**
     * 打印 HTML 内容
     */
    fun printHtml(documentName: String, htmlContent: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val webView = WebView(context)
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter(documentName)
                printManager.print(
                    documentName,
                    printAdapter,
                    PrintAttributes.Builder().build()
                )
            }
        }
        
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
    }
    
    /**
     * 打印入库单
     */
    fun printInboundOrder(
        orderNo: String,
        supplier: String,
        date: String,
        plateNumber: String?,
        items: List<Map<String, Any>>
    ) {
        val html = buildInboundOrderHtml(orderNo, supplier, date, plateNumber, items)
        printHtml("入库单_$orderNo", html)
    }
    
    /**
     * 打印出库单
     */
    fun printOutboundOrder(
        orderNo: String,
        customer: String,
        date: String,
        plateNumber: String?,
        items: List<Map<String, Any>>
    ) {
        val html = buildOutboundOrderHtml(orderNo, customer, date, plateNumber, items)
        printHtml("出库单_$orderNo", html)
    }
    
    /**
     * 构建入库单 HTML
     */
    private fun buildInboundOrderHtml(
        orderNo: String,
        supplier: String,
        date: String,
        plateNumber: String?,
        items: List<Map<String, Any>>
    ): String {
        val itemsHtml = items.joinToString("") { item ->
            """
            <tr>
                <td>${item["name"]}</td>
                <td>${item["quantity"]}</td>
                <td>${item["unit"]}</td>
                <td>${item["weight"] ?: "-"}</td>
                <td>¥${item["price"]}</td>
                <td>¥${item["amount"]}</td>
            </tr>
            """.trimIndent()
        }
        
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; padding: 20px; }
                h1 { text-align: center; color: #1976D2; }
                table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #1976D2; color: white; }
                .info { margin-bottom: 20px; }
                .info p { margin: 5px 0; }
                .total { text-align: right; margin-top: 20px; font-size: 18px; font-weight: bold; }
            </style>
        </head>
        <body>
            <h1>入库单</h1>
            <div class="info">
                <p><strong>单号：</strong>$orderNo</p>
                <p><strong>供应商：</strong>$supplier</p>
                <p><strong>日期：</strong>$date</p>
                ${if (!plateNumber.isNullOrBlank()) "<p><strong>车牌：</strong>$plateNumber</p>" else ""}
            </div>
            <table>
                <thead>
                    <tr>
                        <th>物品</th>
                        <th>数量</th>
                        <th>单位</th>
                        <th>重量</th>
                        <th>单价</th>
                        <th>金额</th>
                    </tr>
                </thead>
                <tbody>
                    $itemsHtml
                </tbody>
            </table>
            <div class="total">
                总计：¥${items.sumOf { (it["amount"] as String).toDouble() }}
            </div>
        </body>
        </html>
        """.trimIndent()
    }
    
    /**
     * 构建出库单 HTML
     */
    private fun buildOutboundOrderHtml(
        orderNo: String,
        customer: String,
        date: String,
        plateNumber: String?,
        items: List<Map<String, Any>>
    ): String {
        val itemsHtml = items.joinToString("") { item ->
            """
            <tr>
                <td>${item["name"]}</td>
                <td>${item["quantity"]}</td>
                <td>${item["unit"]}</td>
                <td>¥${item["price"]}</td>
                <td>¥${item["amount"]}</td>
            </tr>
            """.trimIndent()
        }
        
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; padding: 20px; }
                h1 { text-align: center; color: #FF9800; }
                table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #FF9800; color: white; }
                .info { margin-bottom: 20px; }
                .info p { margin: 5px 0; }
                .total { text-align: right; margin-top: 20px; font-size: 18px; font-weight: bold; }
            </style>
        </head>
        <body>
            <h1>出库单</h1>
            <div class="info">
                <p><strong>单号：</strong>$orderNo</p>
                <p><strong>客户：</strong>$customer</p>
                <p><strong>日期：</strong>$date</p>
                ${if (!plateNumber.isNullOrBlank()) "<p><strong>车牌：</strong>$plateNumber</p>" else ""}
            </div>
            <table>
                <thead>
                    <tr>
                        <th>物品</th>
                        <th>数量</th>
                        <th>单位</th>
                        <th>单价</th>
                        <th>金额</th>
                    </tr>
                </thead>
                <tbody>
                    $itemsHtml
                </tbody>
            </table>
            <div class="total">
                总计：¥${items.sumOf { (it["amount"] as String).toDouble() }}
            </div>
        </body>
        </html>
        """.trimIndent()
    }
}
