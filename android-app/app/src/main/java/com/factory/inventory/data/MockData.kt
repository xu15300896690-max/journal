package com.factory.inventory.data

import com.factory.inventory.data.model.*

/**
 * 本地测试数据
 * 用于开发测试，后续切换为服务器数据
 */
object MockData {
    
    // 模拟用户
    val mockUser = UserInfo(
        id = 1,
        username = "admin",
        real_name = "系统管理员",
        role = "admin"
    )
    
    // 模拟供应商
    val mockSuppliers = listOf(
        Supplier(1, "上海钢铁厂", "张三", "13800138000", "上海市浦东新区"),
        Supplier(2, "五金建材公司", "李四", "13900139000", "苏州市工业园区"),
        Supplier(3, "原材料供应商", "王五", "13700137000", "杭州市西湖区"),
        Supplier(4, "广州贸易公司", "赵六", "13600136000", "广州市天河区"),
        Supplier(5, "北京物资集团", "钱七", "13500135000", "北京市朝阳区")
    )
    
    // 模拟客户
    val mockCustomers = listOf(
        Customer(1, "某建筑公司", "赵六", "13600136000", "南京市鼓楼区"),
        Customer(2, "贸易公司", "钱七", "13500135000", "武汉市武昌区"),
        Customer(3, "制造企业", "孙八", "13400134000", "成都市高新区"),
        Customer(4, "工程公司", "周九", "13300133000", "重庆市渝中区"),
        Customer(5, "零售商家", "吴十", "13200132000", "深圳市南山区")
    )
    
    // 模拟物品
    val mockItems = listOf(
        Item(1, "钢材", "G001", "Φ20mm", "吨", 10),
        Item(2, "螺丝", "L001", "M10×50", "件", 100),
        Item(3, "螺母", "M001", "M10", "件", 100),
        Item(4, "钢板", "GB001", "10mm×1m×2m", "张", 5),
        Item(5, "钢管", "GG001", "Φ50mm×3m", "根", 20),
        Item(6, "角铁", "JT001", "50×50×5mm", "根", 30),
        Item(7, "螺栓", "LS001", "M12×60", "件", 200),
        Item(8, "垫片", "PD001", "Φ10mm", "件", 500)
    )
    
    // 模拟仓库
    val mockWarehouses = listOf(
        Warehouse(1, "主仓库", "WH001", "工厂区 A 栋", "仓库管理员", "13800000001"),
        Warehouse(2, "原材料仓", "WH002", "工厂区 B 栋", "张三", "13800000002"),
        Warehouse(3, "成品仓", "WH003", "工厂区 C 栋", "李四", "13800000003")
    )
    
    // 模拟入库单
    val mockInboundOrders = listOf(
        InboundOrder(
            id = 1,
            order_no = "IN20240324153000",
            supplier_name = "上海钢铁厂",
            warehouse_name = "主仓库",
            plate_number = "沪 A12345",
            driver_name = "刘师傅",
            total_amount = 450000.00,
            status = "completed",
            order_date = "2024-03-24",
            created_at = "2024-03-24 15:30:00"
        ),
        InboundOrder(
            id = 2,
            order_no = "IN20240324140000",
            supplier_name = "五金建材公司",
            warehouse_name = "原材料仓",
            plate_number = "沪 B67890",
            driver_name = "陈师傅",
            total_amount = 25000.00,
            status = "completed",
            order_date = "2024-03-24",
            created_at = "2024-03-24 14:00:00"
        ),
        InboundOrder(
            id = 3,
            order_no = "IN20240323100000",
            supplier_name = "原材料供应商",
            warehouse_name = "主仓库",
            plate_number = "沪 C11111",
            driver_name = "王师傅",
            total_amount = 180000.00,
            status = "completed",
            order_date = "2024-03-23",
            created_at = "2024-03-23 10:00:00"
        )
    )
    
    // 模拟出库单
    val mockOutboundOrders = listOf(
        OutboundOrder(
            id = 1,
            order_no = "OUT20240324160000",
            customer_name = "某建筑公司",
            warehouse_name = "主仓库",
            plate_number = "沪 D22222",
            total_amount = 100000.00,
            status = "completed",
            order_date = "2024-03-24",
            created_at = "2024-03-24 16:00:00"
        ),
        OutboundOrder(
            id = 2,
            order_no = "OUT20240324110000",
            customer_name = "贸易公司",
            warehouse_name = "成品仓",
            plate_number = "沪 E33333",
            total_amount = 50000.00,
            status = "completed",
            order_date = "2024-03-24",
            created_at = "2024-03-24 11:00:00"
        ),
        OutboundOrder(
            id = 3,
            order_no = "OUT20240323150000",
            customer_name = "制造企业",
            warehouse_name = "主仓库",
            plate_number = "沪 F44444",
            total_amount = 75000.00,
            status = "completed",
            order_date = "2024-03-23",
            created_at = "2024-03-23 15:00:00"
        )
    )
    
    // 模拟库存
    val mockInventory = listOf(
        InventoryItem(1, "钢材", "G001", "主仓库", 150, "吨", 10, false),
        InventoryItem(2, "螺丝", "L001", "主仓库", 5000, "件", 100, false),
        InventoryItem(3, "螺母", "M001", "主仓库", 8000, "件", 100, false),
        InventoryItem(4, "钢板", "GB001", "主仓库", 45, "张", 5, false),
        InventoryItem(5, "钢管", "GG001", "主仓库", 180, "根", 20, false),
        InventoryItem(6, "角铁", "JT001", "原材料仓", 25, "根", 30, true), // 预警
        InventoryItem(7, "螺栓", "LS001", "原材料仓", 1500, "件", 200, false),
        InventoryItem(8, "垫片", "PD001", "原材料仓", 8, "件", 500, true) // 预警
    )
    
    // 模拟统计数据
    val mockStats = Stats(
        inbound_today = 475000.00,
        outbound_today = 150000.00,
        inbound_month = 2850000.00,
        outbound_month = 1950000.00,
        profit_today = -325000.00,
        profit_month = -900000.00,
        total_items = 8,
        low_stock_count = 2
    )
}
