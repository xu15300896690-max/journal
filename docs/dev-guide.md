# 工厂出入库管理系统 - 开发文档

## 📱 项目概述

一个完整的工厂出入库管理系统，包含 Android 客户端和 Windows 服务器端。

## ✅ 已完成功能

### Android 客户端

| 页面 | 功能 | 状态 |
|------|------|------|
| 登录页面 | 用户登录、Token 管理 | ✅ |
| 首页 | 4 个功能按钮、最近记录列表 | ✅ |
| 入库管理 | 入库单列表、新建入库单 | ✅ |
| 出库管理 | 出库单列表、新建出库单 | ✅ |
| 库存查询 | 库存列表、预警筛选 | ✅ |
| 统计报表 | 今日/本月统计、库存统计 | ✅ |
| 基础数据 | 供应商/客户/物品管理 | ✅ |

### Windows 服务器端

| 模块 | API 接口 | 状态 |
|------|---------|------|
| 认证 | 登录、注册 | ✅ |
| 基础数据 | 供应商/客户/物品/仓库 | ✅ |
| 入库管理 | 创建入库单、查询列表 | ✅ |
| 出库管理 | 创建出库单、查询列表 | ✅ |
| 库存管理 | 库存查询、库存预警 | ✅ |
| 统计 | 统计数据 | ✅ |

## 📂 项目结构

```
factory-inventory/
├── README.md                    # 项目说明
├── docs/                        # 文档
│   ├── server-setup.md         # 服务器部署指南
│   ├── android-setup.md        # Android 开发指南
│   └── dev-guide.md            # 开发文档（本文件）
├── android-app/                 # Android 客户端
│   └── app/src/main/java/com/factory/inventory/
│       ├── MainActivity.kt      # 主活动 + 导航
│       ├── InventoryApplication.kt
│       ├── data/
│       │   ├── api/            # API 接口
│       │   │   ├── ApiClient.kt
│       │   │   ├── ApiService.kt
│       │   │   └── AuthInterceptor.kt
│       │   └── model/          # 数据模型
│       │       └── Models.kt
│       ├── ui/
│       │   ├── screens/        # 页面
│       │   │   ├── LoginScreen.kt
│       │   │   ├── HomeScreen.kt
│       │   │   ├── InboundScreen.kt
│       │   │   ├── OutboundScreen.kt
│       │   │   ├── InventoryScreen.kt
│       │   │   ├── StatsScreen.kt
│       │   │   └── BaseDataScreen.kt
│       │   └── theme/          # 主题
│       └── util/
│           └── Config.kt       # 配置
└── windows-server/              # Windows 服务器
    ├── app.py                  # Flask 主应用
    └── requirements.txt        # 依赖
```

## 🎨 UI 设计

### 颜色方案

| 用途 | 颜色 | 说明 |
|------|------|------|
| 主色 | #1976D2 | 蓝色，用于入库、主要按钮 |
| 辅色 | #FF9800 | 橙色，用于出库 |
| 成功 | #4CAF50 | 绿色，用于库存、收入 |
| 警告 | #F44336 | 红色，用于预警、支出 |
| 紫色 | #9C27B0 | 紫色，用于统计 |

### 页面布局

#### 首页
- 4 个功能按钮（2x2 网格）
- 最近记录列表（最多 5 条）

#### 列表页面
- 卡片式布局
- 状态徽章
- 浮动操作按钮 (FAB)

#### 表单页面
- 分组显示（车辆信息/物品信息/金额）
- 必填项标识
- 错误提示

## 🔌 API 接口

### 认证
```
POST /api/login          - 用户登录
POST /api/register       - 用户注册
```

### 基础数据
```
GET  /api/suppliers      - 获取供应商列表
POST /api/suppliers      - 添加供应商
GET  /api/customers      - 获取客户列表
POST /api/customers      - 添加客户
GET  /api/items          - 获取物品列表
POST /api/items          - 添加物品
GET  /api/warehouses     - 获取仓库列表
```

### 入库管理
```
POST /api/inbound        - 创建入库单
GET  /api/inbound        - 获取入库单列表
```

### 出库管理
```
POST /api/outbound       - 创建出库单
GET  /api/outbound       - 获取出库单列表
```

### 库存管理
```
GET  /api/inventory      - 获取库存列表
```

### 统计
```
GET  /api/stats          - 获取统计数据
```

## 📋 数据模型

### 入库单
```json
{
  "order_no": "IN20240324153000",
  "warehouse_id": 1,
  "supplier_id": 1,
  "plate_number": "沪 A12345",
  "driver_name": "张三",
  "driver_phone": "13800138000",
  "items": [
    {
      "item_id": 1,
      "quantity": 100,
      "unit": "吨",
      "unit_price": 4500.50,
      "weight": 50.5,
      "weight_unit": "吨"
    }
  ]
}
```

### 出库单
```json
{
  "order_no": "OUT20240324153000",
  "warehouse_id": 1,
  "customer_id": 1,
  "plate_number": "沪 B67890",
  "items": [
    {
      "item_id": 1,
      "quantity": 20,
      "unit_price": 5000.00
    }
  ]
}
```

## 🚀 快速开始

### 1. 启动服务器
```bash
cd windows-server
pip install -r requirements.txt
python app.py
```

### 2. 配置 Android
修改 `Config.kt`:
```kotlin
const val BASE_URL = "http://你的服务器IP:5000"
```

### 3. 编译运行
用 Android Studio 打开 `android-app` 目录

## 📝 待开发功能

### 优先级高
- [ ] 入库单/出库单详情页面
- [ ] 编辑/删除功能
- [ ] 搜索/筛选功能
- [ ] 数据导出（Excel）

### 优先级中
- [ ] 拍照上传（车牌/货物）
- [ ] 扫码枪支持
- [ ] 蓝牙打印机
- [ ] 离线模式

### 优先级低
- [ ] 多仓库调拨
- [ ] 批次管理
- [ ] 保质期管理
- [ ] 消息推送

## 🔧 技术栈

### Android
- Kotlin 1.9+
- Jetpack Compose
- Retrofit 2
- OkHttp 3
- Coroutines

### Server
- Python 3.8+
- Flask 2.0
- SQLAlchemy
- PyJWT

## 📄 License

MIT License
