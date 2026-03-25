# ⚠️ 重要：更新你的 Supabase 配置

## 📋 需要修改的文件

### 1. 编辑 `app/build.gradle.kts`

找到以下位置（约第 24-30 行）：

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

**替换为你的实际配置：**

```kotlin
buildTypes {
    release {
        // ⬇️ 替换为你的 Project URL（注意结尾的 /）
        buildConfigField("String", "SUPABASE_URL", "\"https://abcdefghijklmnop.supabase.co/\"")
        // ⬇️ 替换为你的 anon/public key
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxx\"")
    }
    debug {
        buildConfigField("String", "SUPABASE_URL", "\"https://abcdefghijklmnop.supabase.co/\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxx\"")
    }
}
```

## 🔍 如何获取配置

### 获取 Project URL

1. 登录 https://supabase.com
2. 进入你的项目
3. 左下角 **Settings** → **API**
4. 复制 **Project URL**

格式：`https://xxxxxxxxxxxxx.supabase.co`

### 获取 Anon Key

1. 在同一页面（Settings → API）
2. 找到 **Project API keys**
3. 复制 **anon/public** 那一行（不是 service_role！）

格式：`eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxxxxxxxxxx`

## ⚠️ 注意事项

1. **不要使用 service_role key**
   - ❌ 错误：service_role（管理员密钥，不能放在客户端）
   - ✅ 正确：anon/public（公开密钥，用于客户端）

2. **URL 结尾要有斜杠**
   - ❌ 错误：`https://xxx.supabase.co`
   - ✅ 正确：`https://xxx.supabase.co/`

3. **引号要成对**
   - 确保字符串用双引号包裹
   - Kotlin 字符串中的引号要转义

4. **保存后同步 Gradle**
   - 点击 Android Studio 顶部的 "Sync Now"
   - 或 File → Sync Project with Gradle Files

## ✅ 验证配置

配置完成后，运行应用并检查：

1. **编译无错误**
   ```
   Build → Make Project
   ```

2. **登录界面显示绿色提示**
   - 应该看到 "☁️ Supabase 云端模式"
   - 显示测试账号：admin@factory.com / admin123456

3. **能够成功登录**
   - 使用测试账号登录
   - 进入首页

4. **查看 Logcat 日志**
   ```
   // 成功的日志应该类似：
   D/Supabase: Client initialized
   D/Supabase: Login successful
   ```

## 🐛 常见错误

### 错误 1: "Invalid API key"
**原因**: Key 复制错误
**解决**: 
- 重新从 Dashboard 复制
- 确保没有多余空格
- 确认是 anon key 不是 service_role

### 错误 2: "Connection timeout"
**原因**: URL 错误或网络问题
**解决**:
- 检查 URL 是否正确
- 确保结尾有 `/`
- 检查网络连接

### 错误 3: 编译错误 "Unresolved reference"
**原因**: Gradle 同步失败
**解决**:
- File → Invalidate Caches → Invalidate and Restart
- 重新同步 Gradle

## 📞 需要帮助？

如果遇到问题：
1. 检查 Logcat 日志
2. 查看 Supabase Dashboard → Logs
3. 参考 SUPABASE_QUICKSTART.md

---

**最后更新**: 2026-03-25
