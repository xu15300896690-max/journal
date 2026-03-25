# Supabase 快速接入指南

## 📋 准备工作

- [ ] 注册 Supabase 账号（https://supabase.com）
- [ ] 准备邮箱（用于接收验证码）
- [ ] 记录密码（建议使用密码管理器）

---

## 🔹 第 1 步：创建 Supabase 项目

### 1.1 登录 Supabase

1. 访问 https://supabase.com
2. 点击 **"Start your project"** 或 **"Sign In"**
3. 使用 GitHub/邮箱登录

### 1.2 创建新项目

1. 点击 **"+ New Project"**
2. 填写项目信息：

```
Project name: factory-inventory
Database password: **************  (⚠️ 重要！请记录到密码管理器)
Region: Asia (Singapore) 新加坡（离中国最近）
Pricing plan: Free 免费版
```

3. 点击 **"Create new project"**
4. 等待 2-3 分钟，项目创建完成

### 1.3 获取项目配置

项目创建完成后，在 Dashboard 中：

```
1. 点击左下角 "Settings" (设置图标)
2. 选择 "API"
3. 记录以下信息：

   Project URL: 
   https://xxxxxxxxxxxxx.supabase.co
   
   API Keys → anon/public:
   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...（长字符串）
   
   ⚠️ 不要复制 service_role key（这是管理员密钥，不能放在客户端）
```

**📝 复制并保存：**
```
SUPABASE_URL = https://你的项目 ID.supabase.co
SUPABASE_ANON_KEY = eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 🔹 第 2 步：初始化数据库

### 2.1 打开 SQL Editor

1. 在左侧菜单点击 **"SQL Editor"**
2. 点击 **"+ New query"**

### 2.2 执行迁移脚本

1. 打开项目中的文件：`supabase/migration.sql`
2. **全选复制** 所有内容（约 500 行）
3. 粘贴到 Supabase SQL Editor
4. 点击 **"Run"** 或按 `Ctrl+Enter`

### 2.3 验证执行结果

执行成功后，你会看到：

```
✅ 数据库迁移完成!
📦 表数量：12
🔧 函数数量：3
```

**验证表已创建：**
1. 左侧菜单点击 **"Table Editor"**
2. 应该看到以下表：
   - profiles
   - suppliers
   - customers
   - items
   - item_categories
   - warehouses
   - locations
   - inbound_orders
   - inbound_items
   - outbound_orders
   - outbound_items
   - inventory
   - accounting

---

## 🔹 第 3 步：创建测试用户

### 3.1 手动创建测试账号

1. 左侧菜单 **"Authentication"** → **"Users"**
2. 点击 **"Add user"** → **"Create new user"**
3. 填写信息：

```
Email: admin@factory.com
Password: admin123456
Confirm Password: admin123456
User Role: authenticated
Auto Confirm User: ✅ 勾选（跳过邮箱验证）
```

4. 点击 **"Create user"**

### 3.2 创建用户资料（可选）

在 SQL Editor 中执行：

```sql
-- 为测试用户创建资料
INSERT INTO profiles (id, username, real_name, role)
VALUES (
    (SELECT id FROM auth.users WHERE email = 'admin@factory.com'),
    'admin',
    '系统管理员',
    'admin'
);
```

---

## 🔹 第 4 步：更新 Android 配置

### 4.1 编辑 build.gradle.kts

打开文件：`android-app/app/build.gradle.kts`

找到并修改：

```kotlin
buildTypes {
    release {
        // ⬇️ 修改为你的 Supabase 配置
        buildConfigField("String", "SUPABASE_URL", "\"https://你的项目 ID.supabase.co/\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"你的 anon key\"")
    }
    debug {
        // ⬇️ 修改为你的 Supabase 配置
        buildConfigField("String", "SUPABASE_URL", "\"https://你的项目 ID.supabase.co/\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"你的 anon key\"")
    }
}
```

**示例：**
```kotlin
buildConfigField("String", "SUPABASE_URL", "\"https://abcdefghijklmnop.supabase.co/\"")
buildConfigField("String", "SUPABASE_ANON_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFiY2RlZmdoaWprbG1ub3AiLCJyb2xlIjoiYW5vbiIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoyMDAwMDAwMDAwfQ.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\"")
```

### 4.2 启用 Supabase 模式

打开文件：`android-app/app/src/main/java/com/factory/inventory/util/Config.kt`

修改：
```kotlin
// 从 false 改为 true
const val USE_SUPABASE = true
```

### 4.3 启用 Supabase 依赖

打开文件：`android-app/app/build.gradle.kts`

找到并**取消注释**：

```kotlin
// ⬇️ 取消这些行的注释
implementation(platform("io.github.jan-tennert.supabase:bom:2.0.0"))
implementation("io.github.jan-tennert.supabase:postgrest-kt")
implementation("io.github.jan-tennert.supabase:auth-kt")
implementation("io.ktor:ktor-client-android:2.3.7")
```

---

## 🔹 第 5 步：初始化 Supabase

### 5.1 编辑 InventoryApplication.kt

如果文件不存在，创建：

```kotlin
// android-app/app/src/main/java/com/factory/inventory/InventoryApplication.kt
package com.factory.inventory

import android.app.Application
import com.factory.inventory.data.supabase.SupabaseManager
import com.factory.inventory.util.Config

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

### 5.2 注册 Application

编辑 `android-app/app/src/main/AndroidManifest.xml`：

```xml
<application
    android:name=".InventoryApplication"
    ...>
```

---

## 🔹 第 6 步：更新登录界面

### 6.1 编辑 LoginScreen.kt

找到登录按钮的 onClick 部分，修改为：

```kotlin
Button(
    onClick = {
        isLoading = true
        errorMessage = null
        
        scope.launch {
            try {
                if (Config.USE_SUPABASE) {
                    // ✅ 使用 Supabase 登录
                    SupabaseManager.loginWithEmail(username, password)
                    onLoginSuccess()
                } else {
                    // 使用 Flask 登录（旧方式）
                    val response = ApiClient.getService().login(
                        LoginRequest(username, password)
                    )
                    if (response.isSuccessful && response.body()?.success == true) {
                        ApiClient.setToken(response.body()!!.data!!.token)
                        onLoginSuccess()
                    } else {
                        errorMessage = response.body()?.message ?: "登录失败"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "登录失败：${e.message}"
            } finally {
                isLoading = false
            }
        }
    },
    // ... 其他配置
)
```

---

## 🔹 第 7 步：编译测试

### 7.1 同步 Gradle

在 Android Studio 中：
1. 点击 **"Sync Now"** 或 **"File" → "Sync Project with Gradle Files"**
2. 等待同步完成

### 7.2 编译项目

```
Build → Make Project
或按 Ctrl+F9 (Windows) / Cmd+F9 (Mac)
```

### 7.3 运行测试

1. 连接 Android 设备或启动模拟器
2. 点击 **Run** (▶️) 按钮
3. 使用测试账号登录：
   - 邮箱：`admin@factory.com`
   - 密码：`admin123456`

---

## 🔹 第 8 步：验证功能

### ✅ 登录测试

1. 打开应用
2. 输入测试账号
3. 点击登录
4. 应该成功进入首页

### ✅ 数据查询测试

登录后，检查以下内容：

1. **首页** - 应该显示功能模块和最近记录
2. **基础数据** - 应该能看到默认的供应商/客户数据
3. **入库管理** - 应该能创建入库单
4. **库存查询** - 应该能看到库存数据

### ✅ 在 Supabase 中查看数据

1. 打开 Supabase Dashboard
2. 点击 **"Table Editor"**
3. 选择任意表（如 `suppliers`）
4. 应该能看到数据

---

## 🐛 常见问题排查

### 问题 1: "Invalid API key"

**原因**: SUPABASE_ANON_KEY 复制错误

**解决**:
1. 重新复制 anon key（确保没有多余空格）
2. 检查是否误用了 service_role key
3. 在 Supabase Dashboard 验证 key 是否正确

### 问题 2: "Connection timeout"

**原因**: 网络连接问题

**解决**:
1. 检查设备网络连接
2. 确认 SUPABASE_URL 正确（以 `/` 结尾）
3. 尝试访问 URL 验证：https://你的项目 ID.supabase.co

### 问题 3: "Table does not exist"

**原因**: migration.sql 未执行或执行失败

**解决**:
1. 在 Supabase SQL Editor 重新执行 migration.sql
2. 检查执行日志是否有错误
3. 在 Table Editor 确认表已创建

### 问题 4: "Permission denied"

**原因**: RLS 策略限制

**解决**:
1. 确认用户已登录（authenticated）
2. 检查 RLS 策略是否允许读取
3. 临时禁用 RLS 测试：
   ```sql
   ALTER TABLE suppliers DISABLE ROW LEVEL SECURITY;
   ```

### 问题 5: 编译错误 "Unresolved reference: supabase"

**原因**: 依赖未启用

**解决**:
1. 检查 build.gradle.kts 中 Supabase 依赖是否取消注释
2. 执行 "Sync Project with Gradle Files"
3. 清理并重新构建：Build → Clean Project → Rebuild Project

---

## 📊 验证清单

完成所有步骤后，检查：

- [ ] Supabase 项目创建成功
- [ ] 数据库迁移执行成功（12 张表）
- [ ] 测试用户创建成功
- [ ] Android 配置更新（URL 和 Key）
- [ ] USE_SUPABASE = true
- [ ] Supabase 依赖已启用
- [ ] Application 初始化代码添加
- [ ] 登录界面更新
- [ ] 编译无错误
- [ ] 能成功登录
- [ ] 能查询数据

---

## 🎯 下一步

接入成功后，可以：

1. ✅ 测试所有功能（入库/出库/库存）
2. ✅ 在 Supabase Dashboard 查看实时数据
3. ✅ 使用 Realtime 功能（库存变化推送）
4. ✅ 添加更多用户（在 Authentication → Users）
5. ✅ 配置自定义域名（Pro 功能）

---

## 📞 获取帮助

如果遇到问题：

1. **查看 Supabase Logs**: Dashboard → Logs
2. **检查 Android Logcat**: Android Studio → Logcat
3. **Supabase 文档**: https://supabase.com/docs
4. **Kotlin SDK 文档**: https://supabase.com/docs/reference/kotlin

---

**最后更新**: 2026-03-25
**版本**: 1.0.0
