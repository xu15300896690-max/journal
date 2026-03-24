# Android 客户端开发指南

## 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 26+
- Kotlin 1.9+

## 项目结构

```
android-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/factory/inventory/
│   │   │   ├── ui/              # 界面层 (Jetpack Compose)
│   │   │   │   ├── theme/       # 主题
│   │   │   │   ├── screens/     # 页面
│   │   │   │   └── components/  # 组件
│   │   │   ├── data/            # 数据层
│   │   │   │   ├── api/         # API 接口
│   │   │   │   ├── model/       # 数据模型
│   │   │   │   └── repository/  # 仓库
│   │   │   ├── domain/          # 业务逻辑层
│   │   │   └── util/            # 工具类
│   │   └── res/                 # 资源文件
│   └── build.gradle.kts
└── build.gradle.kts
```

## 快速开始

### 1. 打开项目

用 Android Studio 打开 `android-app` 目录

### 2. 同步 Gradle

等待 Gradle 同步完成

### 3. 配置服务器地址

修改 `Config.kt` 中的服务器地址：

```kotlin
const val BASE_URL = "http://你的服务器IP:5000"
```

### 4. 运行应用

选择模拟器或连接真机，点击 Run

## 功能模块

### 登录模块
- 用户登录
- Token 自动管理
- 记住密码

### 入库管理
- 入库单列表
- 新建入库单
- 车牌识别（可选）
- 拍照上传

### 出库管理
- 出库单列表
- 新建出库单
- 库存检查

### 库存查询
- 实时库存
- 库存预警
- 分类筛选

### 统计报表
- 日报/月报
- 收支图表
- 物品排行

## 技术栈

- **UI:** Jetpack Compose
- **架构:** MVVM
- **网络:** Retrofit + OkHttp
- **依赖注入:** Hilt (可选)
- **本地存储:** DataStore
- **图片加载:** Coil

## 网络权限

在 `AndroidManifest.xml` 中添加：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

## 打包发布

### 生成签名密钥

```bash
keytool -genkey -v -keystore factory-inventory.keystore -alias factory -keyalg RSA -keysize 2048 -validity 10000
```

### 配置签名

在 `app/build.gradle.kts` 中配置签名信息

### 生成 APK

```bash
./gradlew assembleRelease
```

## 常见问题

### Q: 无法连接服务器
A: 检查服务器地址是否正确，确保服务器已启动且网络可达

### Q: Token 过期
A: Token 有效期为当天，过期后重新登录即可

### Q: 图片上传失败
A: 检查相机/存储权限是否已授权

## 后续优化

1. 添加扫码枪支持
2. 添加蓝牙打印机
3. 添加离线模式
4. 添加消息推送
5. 添加数据导出 (Excel)
