# Android 应用界面更新 - xurui Clean Energy 设计

## 更新时间
2026-03-25 11:45 GMT+8

## 已完成更新 ✅

### 1. 主题颜色系统 (`ui/theme/Color.kt`) ✅
- 品牌色：EnergyCyan (#00D4FF), EnergyBlue (#0099CC), EnergyDark (#006699)
- 功能色：InboundBlue, OutboundOrange, InventoryGreen, StatsPurple
- 状态色：StatusSuccess, StatusProcessing, StatusPending, StatusError
- 中性色：BackgroundGray, CardWhite, TextPrimary, TextSecondary
- 深色主题支持

### 2. 主题配置 (`ui/theme/Theme.kt`) ✅
- 更新 Light/Dark ColorScheme
- 状态栏颜色统一为 EnergyBlue
- 支持系统深色模式

### 3. 登录界面 (`ui/screens/LoginScreen.kt`) ✅
- 渐变背景（紫色系）
- 品牌标识：xurui Clean Energy + 闪电图标
- 白色圆角登录卡片
- 带图标的输入框（用户名/密码）
- 错误提示卡片
- 测试环境提示

### 4. 主界面 (`ui/screens/HomeScreen.kt`) ✅
- 顶部栏：品牌标识 + 操作员信息（渐变背景）
- 入库作业主卡片：深色渐变背景、READY TO SCAN 状态、开始扫描按钮
- 功能模块：3 列布局（出库拣货/查询统计/基础数据）
- 最近记录：白色卡片展示 3 条记录

### 5. 入库界面 (`ui/screens/InboundScreen.kt`) ✅
- 蓝色主题 TopAppBar
- 入库单卡片列表（带图标、状态徽章）
- 新建入库单表单（圆角输入框、分组标题）
- 状态徽章：已完成/待处理/已取消/进行中

### 6. 出库界面 (`ui/screens/OutboundScreen.kt`) ✅
- 橙色主题 TopAppBar
- 出库单卡片列表
- 新建出库单表单
- 与入库界面保持一致的设计语言

### 7. 库存查询 (`ui/screens/InventoryScreen.kt`) ✅
- 绿色主题 TopAppBar
- 库存卡片（带预警标识）
- 仅看预警筛选功能
- 库存数量颜色区分（正常/预警）

### 8. 统计报表 (`ui/screens/StatsScreen.kt`) ✅
- 紫色主题 TopAppBar
- 渐变背景统计卡片
- 今日统计/本月统计/库存统计分组
- 利润卡片颜色根据正负值变化

### 9. 基础数据 (`ui/screens/BaseDataScreen.kt`) ✅
- 深色主题 TopAppBar
- 标签页切换（供应商/客户/物品）
- 统一的卡片设计（带圆形图标背景）
- 联系人、电话、地址信息展示

### 10. 数据导出 (`ui/screens/ExportScreen.kt`) ✅
- 蓝色主题 TopAppBar
- 导出类型选择（入库单/出库单）
- 日期范围输入
- 导出结果提示（成功/失败）
- 使用说明卡片

## 设计特点 🎨

### 视觉风格
- **圆角设计**：所有卡片、按钮、输入框使用 12-28dp 圆角
- **图标背景**：圆形图标背景（40dp），半透明色彩
- **渐变效果**：登录页背景、统计卡片使用渐变
- **阴影层次**：卡片 elevation 2-8dp

### 颜色系统
```
品牌色:
- EnergyCyan: #00D4FF
- EnergyBlue: #0099CC
- EnergyDark: #006699

功能色:
- InboundBlue: #1976D2 (入库)
- OutboundOrange: #FF9800 (出库)
- InventoryGreen: #4CAF50 (库存)
- StatsPurple: #9C27B0 (统计)

状态色:
- StatusSuccess: #4CAF50
- StatusProcessing: #2196F3
- StatusPending: #FF9800
- StatusError: #F44336
```

### 组件规范
- **卡片圆角**: 16dp
- **按钮圆角**: 28dp (全圆角)
- **输入框圆角**: 12dp
- **图标背景**: 40dp 圆形
- **间距**: 12dp/16dp/24dp

## 待完成功能 ⏳

### 功能增强
- [ ] 扫码功能集成 (ML Kit)
- [ ] API 数据同步完善
- [ ] 离线数据缓存
- [ ] 打印功能
- [ ] 拍照上传
- [ ] 用户认证完善

### 其他界面
- [ ] 启动页 (Splash Screen)
- [ ] 设置界面
- [ ] 关于界面

## 编译运行 🚀

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高
- JDK 17
- Android SDK 34
- Kotlin 1.9+

### 编译步骤
1. 用 Android Studio 打开 `android-app` 目录
2. 等待 Gradle 同步完成
3. 点击 Run 按钮运行到设备/模拟器

### 配置服务器
修改 `Config.kt` 中的 `BASE_URL` 指向你的服务器地址

## 界面预览 📱

### 登录界面
```
┌─────────────────────────────────┐
│         渐变紫色背景             │
│                                 │
│        [⚡ 品牌图标]              │
│         xurui                   │
│      Clean Energy               │
│                                 │
│    ┌─────────────────────┐      │
│    │  欢迎登录             │      │
│    │  [👤] 用户名 ____    │      │
│    │  [🔒] 密码 ____      │      │
│    │  [登录]              │      │
│    └─────────────────────┘      │
└─────────────────────────────────┘
```

### 主界面
```
┌─────────────────────────────────┐
│ ⚡ xurui      👤 操作员 042     │
│    Clean Energy                 │
├─────────────────────────────────┤
│  ● READY TO SCAN                │
│     入库作业                     │
│  [📷 开始扫描]                  │
├─────────────────────────────────┤
│ [📤出库] [📊查询] [🏢基础]      │
├─────────────────────────────────┤
│ 最近记录              查看全部   │
│ ┌───────────────────────────┐   │
│ │ 📥 PO-94022    入库 ¥xxx  │   │
│ │ 📤 SO-48192    出库 ¥xxx  │   │
│ │ 📋 ST-11029    盘点 待处理 │   │
│ └───────────────────────────┘   │
└─────────────────────────────────┘
```

## 项目位置
`/home/admin/.openclaw/workspace/factory-inventory/android-app/`

## 下一步
- 测试所有界面
- 完善扫码功能
- 优化性能
- 添加动画效果
