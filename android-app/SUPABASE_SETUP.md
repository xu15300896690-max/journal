# Supabase 集成指南 - Android 客户端

## 📦 已完成的工作

### 1. 数据模型 (`data/model/SupabaseModels.kt`)
- ✅ 所有表对应的 Data Class
- ✅ Kotlinx Serialization 注解
- ✅ 关联数据支持（JOIN 查询）

### 2. Supabase 管理器 (`data/supabase/SupabaseManager.kt`)
- ✅ 客户端初始化
- ✅ 用户认证（登录/注册/退出）
- ✅ 基础数据查询（供应商/客户/物品/仓库）
- ✅ 入库管理（列表/创建）
- ✅ 出库管理（列表/创建）
- ✅ 库存管理（列表/实时订阅）
- ✅ 统计查询

### 3. 配置文件更新
- ✅ `Config.kt` - Supabase 配置开关
- ✅ `build.gradle.kts` - 依赖配置

## 🚀 启用步骤

### 1. 在 Supabase 创建项目

```
1. 访问 https://supabase.com
2. 点击 "New Project"
3. 填写项目信息：
   - Name: factory-inventory
   - Database Password: (记录到密码管理器)
   - Region: 选择最近的节点
4. 等待项目创建完成（约 2 分钟）
```

### 2. 获取项目配置

在项目 Dashboard 中：
```
Settings → API
- Project URL: https://xxxxx.supabase.co
- anon/public key: eyJhbGc...（长字符串）
```

### 3. 执行数据库迁移

```
1. 打开 Supabase SQL Editor
2. 复制 `supabase/migration.sql` 内容
3. 粘贴到 SQL Editor
4. 点击 "Run" 执行
5. 确认所有表和函数创建成功
```

### 4. 更新 Android 配置

编辑 `app/build.gradle.kts`:
```kotlin
buildTypes {
    release {
        buildConfigField("String", "SUPABASE_URL", "\"https://your-project.supabase.co/\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"your-anon-key\"")
    }
    debug {
        buildConfigField("String", "SUPABASE_URL", "\"https://your-project.supabase.co/\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"your-anon-key\"")
    }
}
```

### 5. 启用 Supabase 依赖

编辑 `app/build.gradle.kts`,取消注释:
```kotlin
// Supabase
implementation(platform("io.github.jan-tennert.supabase:bom:2.0.0"))
implementation("io.github.jan-tennert.supabase:postgrest-kt")
implementation("io.github.jan-tennert.supabase:auth-kt")
implementation("io.github.jan-tennert.supabase:realtime-kt")
implementation("io.ktor:ktor-client-android:2.3.7")
```

### 6. 切换模式

编辑 `util/Config.kt`:
```kotlin
const val USE_SUPABASE = true  // 从 false 改为 true
```

### 7. 初始化 Supabase

编辑 `InventoryApplication.kt` (或 `MainActivity.kt`):
```kotlin
class InventoryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化 Supabase
        if (Config.USE_SUPABASE) {
            SupabaseManager.init(applicationContext)
        }
    }
}
```

### 8. 更新登录界面

编辑 `LoginScreen.kt`:
```kotlin
Button(onClick = {
    scope.launch {
        try {
            if (Config.USE_SUPABASE) {
                // 使用 Supabase 登录
                SupabaseManager.loginWithEmail(username, password)
                onLoginSuccess()
            } else {
                // 使用 Flask 登录
                val response = ApiClient.getService().login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    ApiClient.setToken(response.body()!!.data!!.token)
                    onLoginSuccess()
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }
})
```

## 📊 数据迁移

### 从 SQLite 迁移到 Supabase

如果需要迁移现有数据：

```kotlin
// 创建迁移工具类
object DataMigrator {
    suspend fun migrateFromSqlite(context: Context) {
        // 1. 读取 SQLite 数据
        // 2. 批量插入到 Supabase
        // 3. 验证数据完整性
    }
}
```

## 🔧 常用操作

### 创建测试账号

在 Supabase Dashboard:
```
Authentication → Users → Add User
- Email: test@factory.com
- Password: test123
- Confirm Password: test123
```

### 测试 API

使用 Supabase API 测试：
```kotlin
// 在应用中测试
val suppliers = SupabaseManager.getSuppliers()
println("供应商数量：${suppliers.size}")

// 或使用 REST API 直接测试
curl -X GET 'https://your-project.supabase.co/rest/v1/suppliers' \
  -H "apikey: your-anon-key" \
  -H "Authorization: Bearer your-anon-key"
```

## 🎯 功能对比

| 功能 | Flask 版本 | Supabase 版本 |
|------|-----------|--------------|
| 登录 | ✅ | ✅ |
| 供应商管理 | ✅ | ✅ |
| 客户管理 | ✅ | ✅ |
| 物品管理 | ✅ | ✅ |
| 入库管理 | ✅ | ✅ (自动更新库存) |
| 出库管理 | ✅ | ✅ (库存检查) |
| 库存查询 | ✅ | ✅ (实时订阅) |
| 统计报表 | ✅ | 🔄 (待实现) |
| 数据导出 | ✅ | 🔄 (待实现) |

## 🐛 常见问题

### Q: 编译错误 "Unresolved reference: supabase"
A: 确保在 `build.gradle.kts` 中取消了 Supabase 依赖的注释

### Q: 登录失败 "Invalid API key"
A: 检查 `SUPABASE_ANON_KEY` 是否正确复制

### Q: 查询返回空数据
A: 检查 RLS 策略是否允许认证用户读取

### Q: 创建入库单失败
A: 检查数据库函数是否创建成功，查看 Supabase Logs

## 📝 下一步

### 待实现功能
- [ ] 统计报表 RPC 函数
- [ ] 数据导出功能
- [ ] 离线模式支持
- [ ] 图片上传（Supabase Storage）
- [ ] 推送通知

### 优化建议
- [ ] 添加缓存层
- [ ] 实现分页加载
- [ ] 添加错误重试机制
- [ ] 优化网络请求

## 🔗 相关文档

- [Supabase Kotlin SDK](https://supabase.com/docs/reference/kotlin)
- [PostgREST 文档](https://postgrest.org/)
- [Supabase Auth](https://supabase.com/docs/guides/auth)
- [Realtime 订阅](https://supabase.com/docs/guides/realtime)

---

**创建时间**: 2026-03-25
**版本**: 1.0.0
