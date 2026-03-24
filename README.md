# 工厂出入库管理系统

一个基于 Kotlin + Flask 的工厂出入库管理系统，包含 Android 客户端和 Windows 服务器端。

## 📱 系统架构

```
┌─────────────────────────────────────────────────┐
│           Android 客户端 (Kotlin)                │
│  Jetpack Compose + MVVM + Retrofit               │
└─────────────────┬───────────────────────────────┘
                  │ HTTP/HTTPS API
                  ▼
┌─────────────────────────────────────────────────┐
│         Windows 服务器 (Flask 后端)              │
│  Python + Flask + SQLAlchemy                     │
└─────────────────┬───────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────┐
│              数据库                              │
│  SQLite (单机) / MySQL (多用户)                  │
└─────────────────────────────────────────────────┘
```

## 🎯 功能模块

### Android 客户端
- 📝 入库管理（车牌、司机、物品、重量、拍照）
- 📤 出库管理（客户、物品、数量、金额）
- 📦 库存查询（实时库存、预警）
- 📊 报表统计（日报、月报、收支）
- 👥 基础数据（供应商、客户、物品、仓库）
- ⚙️ 系统设置（用户、权限、同步）

### Windows 服务器端
- RESTful API 接口
- 用户认证（JWT Token）
- 数据同步
- 多用户权限管理

## 📂 项目结构

```
factory-inventory/
├── android-app/           # Android 客户端
│   ├── app/
│   │   └── src/main/
│   │       ├── java/      # Kotlin 源代码
│   │       └── res/       # 资源文件
│   └── build.gradle
├── windows-server/        # Windows 服务器端
│   ├── app.py            # Flask 主应用
│   ├── models.py         # 数据模型
│   ├── routes/           # API 路由
│   └── requirements.txt  # Python 依赖
├── docs/                 # 文档
└── README.md
```

## 🚀 快速开始

### 1. 启动 Windows 服务器

```bash
cd windows-server
pip install -r requirements.txt
python app.py
```

服务器默认运行在：`http://0.0.0.0:5000`

### 2. 配置 Android 客户端

修改 `android-app/app/src/main/java/.../Config.kt` 中的服务器地址：

```kotlin
const val BASE_URL = "http://你的服务器IP:5000"
```

### 3. 编译 Android 应用

1. 用 Android Studio 打开 `android-app` 目录
2. 同步 Gradle 依赖
3. 运行到模拟器或真机

## 📋 数据库设计

### 主要数据表

| 表名 | 说明 |
|------|------|
| users | 用户表 |
| suppliers | 供应商 |
| customers | 客户 |
| items | 物品/物料 |
| warehouses | 仓库 |
| inbound_orders | 入库单 |
| outbound_orders | 出库单 |
| inventory | 库存 |
| accounting | 记账 |

## 🔐 默认账号

- 用户名：`admin`
- 密码：`admin123`

## 📱 Android 技术要求

- Android 8.0 (API 26) 及以上
- Kotlin 1.9+
- Jetpack Compose
- Minimum SDK: 26

## 🖥️ 服务器要求

- Windows 10/11 或 Linux
- Python 3.8+
- 内存：2GB+
- 存储：根据数据量

## 📄 License

MIT License
