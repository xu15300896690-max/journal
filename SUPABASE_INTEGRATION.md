# Supabase 集成方案

## 📋 概述

将现有的 Flask + SQLite 后端迁移到 Supabase (PostgreSQL + 自动 API + Auth)

## 🎯 为什么选择 Supabase

### 现有架构痛点
- ❌ Flask 服务器需要自己维护
- ❌ SQLite 不支持多用户并发
- ❌ 需要手写所有 API 接口
- ❌ 认证系统需要自己实现
- ❌ 数据同步需要轮询

### Supabase 优势
- ✅ PostgreSQL 云数据库（免费 500MB）
- ✅ 自动生成 RESTful API
- ✅ 内置用户认证（邮箱/手机/第三方登录）
- ✅ Realtime 实时订阅（WebSocket）
- ✅ Row Level Security (RLS) 行级安全
- ✅ 自动备份 + 版本管理
- ✅ 免费额度足够小团队使用

## 📦 数据库迁移

### 1. Supabase 表结构

```sql
-- 用户表 (Supabase Auth 自动管理，扩展表)
CREATE TABLE profiles (
    id UUID REFERENCES auth.users PRIMARY KEY,
    username TEXT UNIQUE,
    real_name TEXT,
    phone TEXT,
    role TEXT DEFAULT 'operator',
    created_at TIMESTAMP DEFAULT NOW()
);

-- 供应商
CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    contact TEXT,
    phone TEXT,
    address TEXT,
    note TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 客户
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    contact TEXT,
    phone TEXT,
    address TEXT,
    note TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 物品分类
CREATE TABLE item_categories (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    parent_id BIGINT,
    sort_order INT DEFAULT 0
);

-- 物品
CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    code TEXT UNIQUE,
    category_id BIGINT REFERENCES item_categories(id),
    spec TEXT,
    unit TEXT DEFAULT '件',
    min_stock INT DEFAULT 0,
    max_stock INT,
    note TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- 仓库
CREATE TABLE warehouses (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    code TEXT UNIQUE,
    address TEXT,
    manager TEXT,
    phone TEXT,
    note TEXT
);

-- 库位
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    warehouse_id BIGINT REFERENCES warehouses(id),
    code TEXT NOT NULL,
    name TEXT,
    zone TEXT
);

-- 入库单
CREATE TABLE inbound_orders (
    id BIGSERIAL PRIMARY KEY,
    order_no TEXT UNIQUE NOT NULL,
    warehouse_id BIGINT REFERENCES warehouses(id),
    supplier_id BIGINT REFERENCES suppliers(id),
    plate_number TEXT,
    driver_name TEXT,
    driver_phone TEXT,
    total_amount DECIMAL DEFAULT 0,
    status TEXT DEFAULT 'pending',
    operator_id UUID REFERENCES profiles(id),
    order_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP,
    note TEXT
);

-- 入库明细
CREATE TABLE inbound_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES inbound_orders(id),
    item_id BIGINT REFERENCES items(id),
    location_id BIGINT REFERENCES locations(id),
    quantity INT NOT NULL,
    unit TEXT,
    gross_weight DECIMAL,
    tare_weight DECIMAL,
    net_weight DECIMAL,
    weight_unit TEXT DEFAULT '吨',
    unit_price DECIMAL DEFAULT 0,
    amount DECIMAL DEFAULT 0,
    batch_no TEXT,
    production_date DATE,
    expiry_date DATE,
    note TEXT
);

-- 出库单
CREATE TABLE outbound_orders (
    id BIGSERIAL PRIMARY KEY,
    order_no TEXT UNIQUE NOT NULL,
    warehouse_id BIGINT REFERENCES warehouses(id),
    customer_id BIGINT REFERENCES customers(id),
    plate_number TEXT,
    driver_name TEXT,
    driver_phone TEXT,
    total_amount DECIMAL DEFAULT 0,
    status TEXT DEFAULT 'pending',
    operator_id UUID REFERENCES profiles(id),
    order_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP,
    note TEXT
);

-- 出库明细
CREATE TABLE outbound_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES outbound_orders(id),
    item_id BIGINT REFERENCES items(id),
    location_id BIGINT REFERENCES locations(id),
    quantity INT NOT NULL,
    unit TEXT,
    unit_price DECIMAL DEFAULT 0,
    amount DECIMAL DEFAULT 0,
    batch_no TEXT,
    note TEXT
);

-- 库存
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT REFERENCES items(id),
    warehouse_id BIGINT REFERENCES warehouses(id),
    location_id BIGINT REFERENCES locations(id),
    quantity INT DEFAULT 0,
    unit TEXT,
    batch_no TEXT,
    last_inbound TIMESTAMP,
    last_outbound TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 记账
CREATE TABLE accounting (
    id BIGSERIAL PRIMARY KEY,
    type TEXT NOT NULL,
    category TEXT,
    amount DECIMAL NOT NULL,
    description TEXT,
    related_type TEXT,
    related_id BIGINT,
    operator_id UUID REFERENCES profiles(id),
    date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT NOW()
);
```

### 2. Row Level Security (RLS)

```sql
-- 启用 RLS
ALTER TABLE suppliers ENABLE ROW LEVEL SECURITY;
ALTER TABLE customers ENABLE ROW LEVEL SECURITY;
ALTER TABLE items ENABLE ROW LEVEL SECURITY;
ALTER TABLE inbound_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE outbound_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE inventory ENABLE ROW LEVEL SECURITY;

-- 创建策略：所有认证用户可读取
CREATE POLICY "Allow authenticated users to read suppliers"
    ON suppliers FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Allow operators to insert suppliers"
    ON suppliers FOR INSERT
    TO authenticated
    WITH CHECK (true);

-- 类似策略应用于其他表...
```

### 3. 数据库函数（自动生成单号）

```sql
CREATE OR REPLACE FUNCTION generate_order_no(prefix TEXT)
RETURNS TEXT AS $$
BEGIN
    RETURN prefix || TO_CHAR(NOW(), 'YYYYMMDDHH24MISS');
END;
$$ LANGUAGE plpgsql;
```

## 📱 Android 客户端集成

### 1. 添加依赖

```kotlin
// app/build.gradle.kts
dependencies {
    // Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:2.0.0"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")
    
    // Ktor (Supabase 依赖)
    implementation("io.ktor:ktor-client-android:2.3.7")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-serialization:2.3.7")
}
```

### 2. Supabase 客户端配置

```kotlin
// Config.kt
object Config {
    // Supabase 配置
    const val SUPABASE_URL = "https://your-project.supabase.co"
    const val SUPABASE_ANON_KEY = "your-anon-key"
    
    // 功能开关
    const val USE_SUPABASE = true  // 切换到 Supabase
    const val USE_LOCAL_DATA = false
}
```

### 3. SupabaseClient 单例

```kotlin
// SupabaseClient.kt
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import com.factory.inventory.util.Config

object SupabaseManager {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = Config.SUPABASE_URL,
        supabaseKey = Config.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Auth)
    }
    
    suspend fun login(email: String, password: String) {
        client.auth.signInWithPassword(email, password)
    }
    
    suspend fun logout() {
        client.auth.signOut()
    }
    
    val isLoggedIn: Boolean
        get() = client.auth.currentSessionOrNull() != null
}
```

### 4. 数据模型 (Data Classes)

```kotlin
// model/InboundOrder.kt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InboundOrder(
    @SerialName("id") val id: Long,
    @SerialName("order_no") val orderNo: String,
    @SerialName("warehouse_id") val warehouseId: Long,
    @SerialName("supplier_id") val supplierId: Long,
    @SerialName("plate_number") val plateNumber: String?,
    @SerialName("driver_name") val driverName: String?,
    @SerialName("total_amount") val totalAmount: Double,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String,
    // 关联数据（通过 JOIN 获取）
    @SerialName("supplier_name") val supplierName: String? = null,
    @SerialName("warehouse_name") val warehouseName: String? = null
)
```

### 5. Repository 层

```kotlin
// repository/InboundRepository.kt
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.select

class InboundRepository {
    
    suspend fun getInboundList(page: Int = 1, perPage: Int = 20): List<InboundOrder> {
        val from = (page - 1) * perPage
        val to = from + perPage - 1
        
        val response = SupabaseManager.client.postgrest["inbound_orders"]
            .select {
                // JOIN 供应商和仓库表
                foreignTable("suppliers") {
                    select("name")
                }
                foreignTable("warehouses") {
                    select("name")
                }
                range(from, to)
                order("created_at", ascending = false)
            }
            .decodeList<InboundOrder>()
        
        return response
    }
    
    suspend fun createInbound(order: InboundOrderRequest): Result<InboundOrder> {
        return try {
            // 调用数据库函数生成单号
            val result = SupabaseManager.client.postgrest.rpc(
                "create_inbound_order",
                buildJsonObject {
                    put("warehouse_id", order.warehouseId)
                    put("supplier_id", order.supplierId)
                    // ... 其他参数
                }
            )
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 6. 实时订阅（库存变化）

```kotlin
// repository/InventoryRepository.kt
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class InventoryRepository {
    
    // 监听库存变化
    fun listenInventoryChanges(warehouseId: Long? = null): Flow<List<Inventory>> = channelFlow {
        val channel = SupabaseManager.client.realtime.channel("inventory:warehouse_id=eq.$warehouseId")
        
        channel.onPostgresChanges(
            event = PostgresChangeEvent.ALL,
            schema = "public",
            table = "inventory"
        ) { payload ->
            // 库存变化时自动刷新
            val updatedList = getInventory(warehouseId)
            send(updatedList)
        }
        
        channel.subscribe()
        
        // 初始数据
        send(getInventory(warehouseId))
    }
}
```

## 🚀 部署步骤

### 1. 创建 Supabase 项目
1. 访问 https://supabase.com
2. 创建新项目
3. 记录 Project URL 和 Anon Key

### 2. 执行 SQL 迁移
1. 在 Supabase SQL Editor 中执行表结构 SQL
2. 执行 RLS 策略 SQL
3. 创建数据库函数

### 3. 配置 Android 客户端
1. 更新 `Config.kt` 中的 Supabase 配置
2. 添加依赖
3. 切换 `USE_SUPABASE = true`

### 4. 测试
1. 测试登录/注册
2. 测试 CRUD 操作
3. 测试实时订阅

## 💰 成本估算

### Supabase 免费额度
- 数据库：500MB
- 带宽：2GB/月
- 认证用户：50,000/月
- API 请求：无限

### 适用场景
- ✅ 小型仓库（< 10 用户）
- ✅ 数据量 < 500MB
- ✅ 初创团队

### Pro 版本 ($25/月)
- 数据库：8GB
- 带宽：50GB/月
- 邮件支持
- 备份保留 30 天

## 📊 迁移对比

| 功能 | Flask 版本 | Supabase 版本 |
|------|-----------|--------------|
| 数据库 | SQLite | PostgreSQL |
| API 代码 | ~2000 行 | ~200 行 |
| 认证 | 手写 JWT | 内置 Auth |
| 实时性 | 轮询 | WebSocket |
| 部署 | 自己维护 | 托管服务 |
| 成本 | 服务器费用 | 免费/$25 月 |

## 🔧 代码改动量

### 需要修改的文件
1. `Config.kt` - 添加 Supabase 配置
2. `ApiClient.kt` → `SupabaseManager.kt` - 新的客户端
3. `repository/` - 新的 Repository 层
4. `model/` - 添加序列化注解
5. `LoginScreen.kt` - 调用 Supabase Auth
6. 各 Screen - 使用新的 Repository

### 保留的部分
- ✅ 所有 UI 界面
- ✅ 主题颜色
- ✅ 导航逻辑
- ✅ 业务逻辑

## 📝 下一步

1. 创建 Supabase 项目
2. 执行 SQL 迁移脚本
3. 更新 Android 依赖
4. 实现 SupabaseManager
5. 迁移 Repository 层
6. 测试验证

需要我帮你实现具体的代码吗？
