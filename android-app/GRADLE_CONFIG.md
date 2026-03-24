## Gradle 配置说明

### 版本兼容性

| 组件 | 版本 | 说明 |
|------|------|------|
| Gradle | 8.4 | 支持 Java 17-21 |
| Android Gradle Plugin | 8.2.0 | 兼容 Gradle 8.4 |
| Kotlin | 1.9.20 | 兼容 AGP 8.2 |
| JVM Target | 17 | Android 推荐 |
| Compose Compiler | 1.5.4 | 兼容 Kotlin 1.9.20 |

### 如果遇到版本问题

#### 方法 1：更新 Gradle
```bash
# 在 Android Studio 终端执行
./gradlew wrapper --gradle-version=8.4
```

#### 方法 2：修改 gradle-wrapper.properties
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
```

#### 方法 3：清理并重新同步
```bash
# 停止 Gradle
./gradlew --stop

# 清理项目
./gradlew clean

# 重新同步
File → Sync Project with Gradle Files
```

### Java 版本要求

- **Gradle 8.4:** Java 17-21
- **AGP 8.2:** Java 17+
- **Kotlin 1.9.20:** Java 8+

### Android Studio 设置

1. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
   - Gradle JDK: 选择 JDK 17 或 21

2. **File → Project Structure → SDK Location**
   - JDK Location: 确保指向有效的 JDK

3. **File → Invalidate Caches → Invalidate and Restart**
   - 清理缓存并重启
