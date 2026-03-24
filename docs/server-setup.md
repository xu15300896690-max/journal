# Windows 服务器部署指南

## 环境要求

- Windows 10/11
- Python 3.8+
- 内存：2GB+

## 安装步骤

### 1. 安装 Python

下载并安装 Python 3.8+：https://www.python.org/downloads/

安装时勾选 "Add Python to PATH"

### 2. 安装依赖

```bash
cd windows-server
pip install -r requirements.txt
```

### 3. 启动服务

```bash
python app.py
```

### 4. 配置防火墙

允许 5000 端口：
```bash
netsh advfirewall firewall add rule name="Factory Inventory" dir=in action=allow protocol=TCP localport=5000
```

### 5. 设置开机启动

创建 `start_server.bat`：
```batch
@echo off
cd /d %~dp0
python app.py
pause
```

将快捷方式放入启动文件夹。

## 默认账号

- 用户名：`admin`
- 密码：`admin123`

## API 测试

```bash
# 登录
curl -X POST http://localhost:5000/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取统计数据
curl http://localhost:5000/api/stats \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 数据备份

数据库文件位于：`windows-server/instance/factory_inventory.db`

定期备份此文件即可。

## 生产环境建议

1. 使用 MySQL 替代 SQLite
2. 配置 Nginx 反向代理
3. 启用 HTTPS
4. 修改默认密码
5. 配置日志记录
