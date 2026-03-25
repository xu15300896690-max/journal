# 🔧 修复 jlink.exe 错误 - Java 21 兼容性问题

## ❌ 错误信息

```
Error while executing process D:\Program Files\Android\Android Studio\jbr\bin\jlink.exe
Process 'command 'D:\Program Files\Android\Android Studio\jbr\bin\jlink.exe'' finished with non-zero exit value 1
```

## 🎯 问题原因

**Android Gradle Plugin (AGP) 8.2.0 与 Java 21 不兼容**

- **AGP 8.2.0**: 支持 Java 8-17
- **AGP 8.5.0+**: 支持 Java 8-21
- **当前 Java**: 21 ❌

## ✅ 解决方案

### 方案 1: 升级 AGP 到 8.5.0（推荐，已应用）

**已更新的文件:**

1. **build.gradle.kts**
   ```kotlin
   // 升级 AGP 版本
   id("com.android.application") version "8.5.0" apply false
   ```

2. **gradle-wrapper.properties**
   ```properties
   # 升级 Gradle 版本
   distributionUrl=https\://services.gradle.org/distributions/gradle-8.6-bin.zip
   ```

**AGP 与 Gradle 版本对应:**
| AGP 版本 | Gradle 版本 | Java 支持 |
|---------|-----------|---------|
| 8.2.0 | 8.2 | Java 8-17 |
| 8.5.0 | 8.6 | Java 8-21 ✅ |

### 方案 2: 降级到 Java 17（备选）

如果不想升级 AGP，可以降级 Java：

1. **下载 Java 17**
   - Android Studio: File → Settings → Build Tools → Gradle → Gradle JDK
   - 选择 "Download JDK" → 选择 Java 17

2. **或者修改 gradle.properties**
   ```properties
   org.gradle.java.home=C:\\Program Files\\Java\\jdk-17
   ```

## 🚀 应用修复后

1. **清理项目**
   ```
   Build → Clean Project
   ```

2. **重新构建**
   ```
   Build → Rebuild Project
   ```

3. **同步 Gradle**
   ```
   File → Sync Project with Gradle Files
   ```

## 💡 版本推荐

对于新项目（2024 年）：

| 组件 | 推荐版本 |
|------|---------|
| Android Studio | 2024.1+ (Jellyfish) |
| AGP | 8.5.0+ |
| Gradle | 8.6+ |
| Java | 17 或 21 |
| Kotlin | 1.9.20+ |
| compileSdk | 34 |

## 📝 注意事项

### AGP 8.5 变更

AGP 8.5 引入了一些变更：

1. **命名空间迁移**
   ```kotlin
   // app/build.gradle.kts
   android {
       namespace = "com.factory.inventory"
       // ...
   }
   ```

2. **JDK 配置**
   ```kotlin
   compileOptions {
       sourceCompatibility = JavaVersion.VERSION_17
       targetCompatibility = JavaVersion.VERSION_17
   }
   ```

### 如果还有问题

1. **清理 Gradle 缓存**
   ```bash
   # 关闭 Android Studio
   # 删除以下文件夹：
   C:\Users\你的用户名\.gradle\caches
   C:\Users\你的用户名\AppData\Local\Google\AndroidStudio*\cache
   ```

2. **重启 Android Studio**
   ```
   File → Invalidate Caches → Invalidate and Restart
   ```

3. **检查 Java 版本**
   ```bash
   java -version
   # 应该显示 17 或 21
   ```

---

**修复时间**: 2026-03-25
**状态**: ✅ 已应用方案 1（升级 AGP）
