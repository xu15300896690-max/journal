# 🔧 修复 Gradle JVM 版本不兼容问题

## ❌ 错误信息

```
Incompatible Gradle JVM version
The project's Gradle version 8.4 is incompatible with the Gradle JVM version 21
Gradle 8.4 supports Java versions between 1.8 and 20
```

## 🎯 问题原因

- **Gradle 版本**: 8.4
- **当前 JVM**: Java 21
- **支持范围**: Java 8-20
- **不兼容**: Java 21 > Java 20

## ✅ 解决方案

### 方法 1: Android Studio 中修改（推荐）

1. **打开设置**
   - Windows/Linux: `File → Settings`
   - macOS: `Android Studio → Preferences`

2. **导航到 Gradle 设置**
   ```
   Build, Execution, Deployment
   → Build Tools
   → Gradle
   ```

3. **修改 Gradle JDK**
   - 找到 **"Gradle JDK"** 选项
   - 从下拉菜单选择 **Java 17** (推荐) 或 **Java 11**
   - 如果没有，点击 **"Download JDK"** 下载

4. **应用并同步**
   - 点击 **"Apply"**
   - 点击 **"OK"**
   - 点击 **"Sync Now"**

### 方法 2: 修改 gradle.properties

已在项目根目录的 `gradle.properties` 中添加：

```properties
# Gradle JVM version (compatible with Gradle 8.4)
org.gradle.java.home=/usr/lib/jvm/java-17-openjdk
```

**注意**: 需要根据实际 Java 安装路径修改：

- **Windows**: `C:\\Program Files\\Java\\jdk-17`
- **macOS**: `/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home`
- **Linux**: `/usr/lib/jvm/java-17-openjdk`

### 方法 3: 升级 Gradle（可选）

升级到 Gradle 8.5+ 可以支持 Java 21：

**编辑**: `android-app/gradle/wrapper/gradle-wrapper.properties`
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

---

## 📋 推荐的 Java 版本

对于 Android 开发：

| Java 版本 | 推荐度 | 说明 |
|---------|--------|------|
| Java 17 | ⭐⭐⭐⭐⭐ | LTS，Android Studio 推荐 |
| Java 11 | ⭐⭐⭐⭐ | LTS，稳定 |
| Java 8  | ⭐⭐⭐ | 最低要求 |
| Java 21 | ❌ | Gradle 8.4 不支持 |

---

## 🚀 验证修复

修复后，在 Android Studio 中：

1. **File → Sync Project with Gradle Files**
2. 检查 **Build** 窗口是否还有错误
3. 运行 `Build → Make Project`

---

## 💡 查看当前 Java 版本

### 命令行
```bash
java -version
```

### Android Studio
```
File → Project Structure → SDK Location
→ Gradle Settings → Gradle JDK
```

---

## 📞 需要帮助？

如果还有问题：
1. 检查 Android Studio 版本（推荐 2023.1+）
2. 清理缓存：`File → Invalidate Caches → Invalidate and Restart`
3. 删除 `.gradle` 文件夹重新同步

---

**最后更新**: 2026-03-25
