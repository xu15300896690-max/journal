"""
工厂出入库管理系统 - Windows 服务器端
Factory Inventory Management System - Server

运行：python app.py
访问：http://0.0.0.0:5000
"""

from flask import Flask, request, jsonify, send_file
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from datetime import datetime, date
from functools import wraps
import jwt
import hashlib
import os

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///factory_inventory.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SECRET_KEY'] = 'factory-inventory-secret-key-2024'
app.config['UPLOAD_FOLDER'] = 'uploads'

CORS(app)
db = SQLAlchemy(app)

# 创建上传目录
os.makedirs(app.config['UPLOAD_FOLDER'], exist_ok=True)

# ==================== 数据模型 ====================

class User(db.Model):
    """用户表"""
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password = db.Column(db.String(100), nullable=False)
    real_name = db.Column(db.String(50))
    phone = db.Column(db.String(20))
    role = db.Column(db.String(20), default='operator')  # admin/manager/operator/viewer
    created_at = db.Column(db.DateTime, default=datetime.now)
    is_active = db.Column(db.Boolean, default=True)

class Supplier(db.Model):
    """供应商表"""
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    contact = db.Column(db.String(50))
    phone = db.Column(db.String(20))
    address = db.Column(db.String(200))
    note = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.now)

class Customer(db.Model):
    """客户表"""
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    contact = db.Column(db.String(50))
    phone = db.Column(db.String(20))
    address = db.Column(db.String(200))
    note = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.now)

class ItemCategory(db.Model):
    """物品分类表"""
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False)
    parent_id = db.Column(db.Integer)
    sort_order = db.Column(db.Integer, default=0)

class Item(db.Model):
    """物品/物料表"""
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    code = db.Column(db.String(50), unique=True)  # 物品编码
    category_id = db.Column(db.Integer, db.ForeignKey('item_category.id'))
    spec = db.Column(db.String(100))  # 规格型号
    unit = db.Column(db.String(20), default='件')  # 基本单位
    min_stock = db.Column(db.Integer, default=0)  # 最低库存
    max_stock = db.Column(db.Integer)  # 最高库存
    note = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=datetime.now)

class Warehouse(db.Model):
    """仓库表"""
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), nullable=False)
    code = db.Column(db.String(20), unique=True)
    address = db.Column(db.String(200))
    manager = db.Column(db.String(50))
    phone = db.Column(db.String(20))
    note = db.Column(db.Text)

class Location(db.Model):
    """库位表"""
    id = db.Column(db.Integer, primary_key=True)
    warehouse_id = db.Column(db.Integer, db.ForeignKey('warehouse.id'))
    code = db.Column(db.String(20), nullable=False)  # 库位编码
    name = db.Column(db.String(50))
    zone = db.Column(db.String(20))  # 区域

class InboundOrder(db.Model):
    """入库单表"""
    id = db.Column(db.Integer, primary_key=True)
    order_no = db.Column(db.String(50), unique=True, nullable=False)  # 入库单号
    warehouse_id = db.Column(db.Integer, db.ForeignKey('warehouse.id'))
    supplier_id = db.Column(db.Integer, db.ForeignKey('supplier.id'))
    
    # 车辆信息
    plate_number = db.Column(db.String(20))  # 车牌号
    driver_name = db.Column(db.String(50))  # 司机姓名
    driver_phone = db.Column(db.String(20))  # 司机电话
    
    # 金额信息
    total_amount = db.Column(db.Float, default=0)
    
    # 状态
    status = db.Column(db.String(20), default='pending')  # pending/completed/cancelled
    
    # 操作人
    operator_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    
    # 时间
    order_date = db.Column(db.Date, default=date.today)
    created_at = db.Column(db.DateTime, default=datetime.now)
    completed_at = db.Column(db.DateTime)
    
    note = db.Column(db.Text)

class InboundItem(db.Model):
    """入库单明细表"""
    id = db.Column(db.Integer, primary_key=True)
    order_id = db.Column(db.Integer, db.ForeignKey('inbound_order.id'))
    item_id = db.Column(db.Integer, db.ForeignKey('item.id'))
    location_id = db.Column(db.Integer, db.ForeignKey('location.id'))
    
    quantity = db.Column(db.Integer, nullable=False)
    unit = db.Column(db.String(20))  # 单位
    
    # 重量信息
    gross_weight = db.Column(db.Float)  # 毛重
    tare_weight = db.Column(db.Float)   # 皮重
    net_weight = db.Column(db.Float)    # 净重
    weight_unit = db.Column(db.String(10), default='吨')
    
    unit_price = db.Column(db.Float, default=0)
    amount = db.Column(db.Float, default=0)
    
    batch_no = db.Column(db.String(50))  # 批次号
    production_date = db.Column(db.Date)  # 生产日期
    expiry_date = db.Column(db.Date)  # 有效期
    
    note = db.Column(db.Text)

class OutboundOrder(db.Model):
    """出库单表"""
    id = db.Column(db.Integer, primary_key=True)
    order_no = db.Column(db.String(50), unique=True, nullable=False)
    warehouse_id = db.Column(db.Integer, db.ForeignKey('warehouse.id'))
    customer_id = db.Column(db.Integer, db.ForeignKey('customer.id'))
    
    # 车辆信息
    plate_number = db.Column(db.String(20))
    driver_name = db.Column(db.String(50))
    driver_phone = db.Column(db.String(20))
    
    total_amount = db.Column(db.Float, default=0)
    status = db.Column(db.String(20), default='pending')
    operator_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    order_date = db.Column(db.Date, default=date.today)
    created_at = db.Column(db.DateTime, default=datetime.now)
    completed_at = db.Column(db.DateTime)
    note = db.Column(db.Text)

class OutboundItem(db.Model):
    """出库单明细表"""
    id = db.Column(db.Integer, primary_key=True)
    order_id = db.Column(db.Integer, db.ForeignKey('outbound_order.id'))
    item_id = db.Column(db.Integer, db.ForeignKey('item.id'))
    location_id = db.Column(db.Integer, db.ForeignKey('location.id'))
    
    quantity = db.Column(db.Integer, nullable=False)
    unit = db.Column(db.String(20))
    unit_price = db.Column(db.Float, default=0)
    amount = db.Column(db.Float, default=0)
    
    batch_no = db.Column(db.String(50))
    note = db.Column(db.Text)

class Inventory(db.Model):
    """库存表"""
    id = db.Column(db.Integer, primary_key=True)
    item_id = db.Column(db.Integer, db.ForeignKey('item.id'))
    warehouse_id = db.Column(db.Integer, db.ForeignKey('warehouse.id'))
    location_id = db.Column(db.Integer, db.ForeignKey('location.id'))
    
    quantity = db.Column(db.Integer, default=0)
    unit = db.Column(db.String(20))
    
    # 批次库存（可选）
    batch_no = db.Column(db.String(50))
    
    last_inbound = db.Column(db.DateTime)
    last_outbound = db.Column(db.DateTime)
    updated_at = db.Column(db.DateTime, default=datetime.now, onupdate=datetime.now)

class Accounting(db.Model):
    """记账表"""
    id = db.Column(db.Integer, primary_key=True)
    type = db.Column(db.String(10), nullable=False)  # income/expense
    category = db.Column(db.String(50))  # 分类
    amount = db.Column(db.Float, nullable=False)
    description = db.Column(db.Text)
    related_type = db.Column(db.String(20))  # inbound/outbound
    related_id = db.Column(db.Integer)
    operator_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    date = db.Column(db.Date, default=date.today)
    created_at = db.Column(db.DateTime, default=datetime.now)

# ==================== 工具函数 ====================

def generate_order_no(prefix):
    """生成单号"""
    now = datetime.now()
    return f"{prefix}{now.strftime('%Y%m%d%H%M%S')}"

def hash_password(password):
    """密码加密"""
    return hashlib.sha256(password.encode()).hexdigest()

def token_required(f):
    """JWT Token 验证装饰器"""
    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.headers.get('Authorization')
        if not token:
            return jsonify({'success': False, 'message': '缺少 Token'}), 401
        
        try:
            if token.startswith('Bearer '):
                token = token[7:]
            data = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])
            current_user = User.query.get(data['user_id'])
            if not current_user:
                return jsonify({'success': False, 'message': '用户不存在'}), 401
        except:
            return jsonify({'success': False, 'message': 'Token 无效'}), 401
        
        return f(current_user, *args, **kwargs)
    return decorated

# ==================== 认证接口 ====================

@app.route('/api/login', methods=['POST'])
def login():
    """用户登录"""
    data = request.json
    username = data.get('username')
    password = data.get('password')
    
    user = User.query.filter_by(username=username).first()
    if not user or user.password != hash_password(password):
        return jsonify({'success': False, 'message': '用户名或密码错误'}), 401
    
    if not user.is_active:
        return jsonify({'success': False, 'message': '账号已被禁用'}), 401
    
    token = jwt.encode({
        'user_id': user.id,
        'username': user.username,
        'role': user.role,
        'exp': datetime.utcnow().replace(hour=23, minute=59, second=59)
    }, app.config['SECRET_KEY'], algorithm='HS256')
    
    return jsonify({
        'success': True,
        'token': token,
        'user': {
            'id': user.id,
            'username': user.username,
            'real_name': user.real_name,
            'role': user.role
        }
    })

@app.route('/api/register', methods=['POST'])
def register():
    """用户注册（仅管理员）"""
    data = request.json
    username = data.get('username')
    password = data.get('password')
    real_name = data.get('real_name')
    phone = data.get('phone')
    role = data.get('role', 'operator')
    
    if User.query.filter_by(username=username).first():
        return jsonify({'success': False, 'message': '用户名已存在'}), 400
    
    user = User(
        username=username,
        password=hash_password(password),
        real_name=real_name,
        phone=phone,
        role=role
    )
    db.session.add(user)
    db.session.commit()
    
    return jsonify({'success': True, 'message': '注册成功'})

# ==================== 基础数据接口 ====================

@app.route('/api/suppliers', methods=['GET'])
@token_required
def get_suppliers(current_user):
    """获取供应商列表"""
    suppliers = Supplier.query.all()
    return jsonify({
        'success': True,
        'data': [{
            'id': s.id,
            'name': s.name,
            'contact': s.contact,
            'phone': s.phone,
            'address': s.address
        } for s in suppliers]
    })

@app.route('/api/suppliers', methods=['POST'])
@token_required
def add_supplier(current_user):
    """添加供应商"""
    data = request.json
    supplier = Supplier(
        name=data.get('name'),
        contact=data.get('contact'),
        phone=data.get('phone'),
        address=data.get('address'),
        note=data.get('note')
    )
    db.session.add(supplier)
    db.session.commit()
    return jsonify({'success': True, 'id': supplier.id})

@app.route('/api/customers', methods=['GET'])
@token_required
def get_customers(current_user):
    """获取客户列表"""
    customers = Customer.query.all()
    return jsonify({
        'success': True,
        'data': [{
            'id': c.id,
            'name': c.name,
            'contact': c.contact,
            'phone': c.phone,
            'address': c.address
        } for c in customers]
    })

@app.route('/api/customers', methods=['POST'])
@token_required
def add_customer(current_user):
    """添加客户"""
    data = request.json
    customer = Customer(
        name=data.get('name'),
        contact=data.get('contact'),
        phone=data.get('phone'),
        address=data.get('address'),
        note=data.get('note')
    )
    db.session.add(customer)
    db.session.commit()
    return jsonify({'success': True, 'id': customer.id})

@app.route('/api/items', methods=['GET'])
@token_required
def get_items(current_user):
    """获取物品列表"""
    items = Item.query.all()
    return jsonify({
        'success': True,
        'data': [{
            'id': i.id,
            'name': i.name,
            'code': i.code,
            'spec': i.spec,
            'unit': i.unit,
            'min_stock': i.min_stock
        } for i in items]
    })

@app.route('/api/items', methods=['POST'])
@token_required
def add_item(current_user):
    """添加物品"""
    data = request.json
    item = Item(
        name=data.get('name'),
        code=data.get('code'),
        spec=data.get('spec'),
        unit=data.get('unit', '件'),
        min_stock=data.get('min_stock', 0),
        note=data.get('note')
    )
    db.session.add(item)
    db.session.commit()
    return jsonify({'success': True, 'id': item.id})

@app.route('/api/warehouses', methods=['GET'])
@token_required
def get_warehouses(current_user):
    """获取仓库列表"""
    warehouses = Warehouse.query.all()
    return jsonify({
        'success': True,
        'data': [{
            'id': w.id,
            'name': w.name,
            'code': w.code,
            'manager': w.manager,
            'phone': w.phone
        } for w in warehouses]
    })

# ==================== 入库管理接口 ====================

@app.route('/api/inbound', methods=['POST'])
@token_required
def create_inbound(current_user):
    """创建入库单"""
    data = request.json
    
    # 生成单号
    order_no = generate_order_no('IN')
    
    # 创建入库单
    order = InboundOrder(
        order_no=order_no,
        warehouse_id=data.get('warehouse_id'),
        supplier_id=data.get('supplier_id'),
        plate_number=data.get('plate_number'),
        driver_name=data.get('driver_name'),
        driver_phone=data.get('driver_phone'),
        operator_id=current_user.id,
        note=data.get('note')
    )
    db.session.add(order)
    db.session.flush()
    
    # 添加入库明细
    items = data.get('items', [])
    total_amount = 0
    
    for item_data in items:
        inbound_item = InboundItem(
            order_id=order.id,
            item_id=item_data.get('item_id'),
            location_id=item_data.get('location_id'),
            quantity=item_data.get('quantity', 0),
            unit=item_data.get('unit', '件'),
            gross_weight=item_data.get('gross_weight'),
            tare_weight=item_data.get('tare_weight'),
            net_weight=item_data.get('net_weight'),
            weight_unit=item_data.get('weight_unit', '吨'),
            unit_price=item_data.get('unit_price', 0),
            amount=item_data.get('quantity', 0) * item_data.get('unit_price', 0),
            batch_no=item_data.get('batch_no'),
            note=item_data.get('note')
        )
        db.session.add(inbound_item)
        total_amount += inbound_item.amount
        
        # 更新库存
        inventory = Inventory.query.filter_by(
            item_id=item_data.get('item_id'),
            warehouse_id=data.get('warehouse_id'),
            location_id=item_data.get('location_id')
        ).first()
        
        if inventory:
            inventory.quantity += item_data.get('quantity', 0)
            inventory.last_inbound = datetime.now()
        else:
            inventory = Inventory(
                item_id=item_data.get('item_id'),
                warehouse_id=data.get('warehouse_id'),
                location_id=item_data.get('location_id'),
                quantity=item_data.get('quantity', 0),
                unit=item_data.get('unit', '件'),
                batch_no=item_data.get('batch_no'),
                last_inbound=datetime.now()
            )
            db.session.add(inventory)
    
    order.total_amount = total_amount
    order.status = 'completed'
    order.completed_at = datetime.now()
    
    # 添加入库记账
    accounting = Accounting(
        type='expense',
        category='采购',
        amount=total_amount,
        description=f'入库单 {order_no}',
        related_type='inbound',
        related_id=order.id,
        operator_id=current_user.id
    )
    db.session.add(accounting)
    
    db.session.commit()
    
    return jsonify({'success': True, 'order_no': order_no, 'id': order.id})

@app.route('/api/inbound', methods=['GET'])
@token_required
def get_inbound_list(current_user):
    """获取入库单列表"""
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 20, type=int)
    status = request.args.get('status')
    start_date = request.args.get('start_date')
    end_date = request.args.get('end_date')
    
    query = InboundOrder.query
    
    if status:
        query = query.filter_by(status=status)
    if start_date:
        query = query.filter(InboundOrder.order_date >= start_date)
    if end_date:
        query = query.filter(InboundOrder.order_date <= end_date)
    
    orders = query.order_by(InboundOrder.created_at.desc()).paginate(page, per_page, False)
    
    return jsonify({
        'success': True,
        'data': [{
            'id': o.id,
            'order_no': o.order_no,
            'supplier_name': Supplier.query.get(o.supplier_id).name if o.supplier_id else '',
            'warehouse_name': Warehouse.query.get(o.warehouse_id).name if o.warehouse_id else '',
            'plate_number': o.plate_number,
            'driver_name': o.driver_name,
            'total_amount': o.total_amount,
            'status': o.status,
            'order_date': o.order_date.strftime('%Y-%m-%d'),
            'created_at': o.created_at.strftime('%Y-%m-%d %H:%M')
        } for o in orders.items],
        'total': orders.total,
        'pages': orders.pages
    })

# ==================== 出库管理接口 ====================

@app.route('/api/outbound', methods=['POST'])
@token_required
def create_outbound(current_user):
    """创建出库单"""
    data = request.json
    
    order_no = generate_order_no('OUT')
    
    order = OutboundOrder(
        order_no=order_no,
        warehouse_id=data.get('warehouse_id'),
        customer_id=data.get('customer_id'),
        plate_number=data.get('plate_number'),
        driver_name=data.get('driver_name'),
        driver_phone=data.get('driver_phone'),
        operator_id=current_user.id,
        note=data.get('note')
    )
    db.session.add(order)
    db.session.flush()
    
    items = data.get('items', [])
    total_amount = 0
    
    for item_data in items:
        # 检查库存
        inventory = Inventory.query.filter_by(
            item_id=item_data.get('item_id'),
            warehouse_id=data.get('warehouse_id')
        ).first()
        
        if not inventory or inventory.quantity < item_data.get('quantity', 0):
            db.session.rollback()
            return jsonify({
                'success': False,
                'message': f'物品 {item_data.get("item_name")} 库存不足'
            }), 400
        
        outbound_item = OutboundItem(
            order_id=order.id,
            item_id=item_data.get('item_id'),
            location_id=item_data.get('location_id'),
            quantity=item_data.get('quantity', 0),
            unit=item_data.get('unit', '件'),
            unit_price=item_data.get('unit_price', 0),
            amount=item_data.get('quantity', 0) * item_data.get('unit_price', 0),
            batch_no=item_data.get('batch_no'),
            note=item_data.get('note')
        )
        db.session.add(outbound_item)
        total_amount += outbound_item.amount
        
        # 扣减库存
        inventory.quantity -= item_data.get('quantity', 0)
        inventory.last_outbound = datetime.now()
    
    order.total_amount = total_amount
    order.status = 'completed'
    order.completed_at = datetime.now()
    
    # 添加入库记账
    accounting = Accounting(
        type='income',
        category='销售',
        amount=total_amount,
        description=f'出库单 {order_no}',
        related_type='outbound',
        related_id=order.id,
        operator_id=current_user.id
    )
    db.session.add(accounting)
    
    db.session.commit()
    
    return jsonify({'success': True, 'order_no': order_no, 'id': order.id})

@app.route('/api/outbound', methods=['GET'])
@token_required
def get_outbound_list(current_user):
    """获取出库单列表"""
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 20, type=int)
    
    orders = OutboundOrder.query.order_by(OutboundOrder.created_at.desc()).paginate(page, per_page, False)
    
    return jsonify({
        'success': True,
        'data': [{
            'id': o.id,
            'order_no': o.order_no,
            'customer_name': Customer.query.get(o.customer_id).name if o.customer_id else '',
            'warehouse_name': Warehouse.query.get(o.warehouse_id).name if o.warehouse_id else '',
            'plate_number': o.plate_number,
            'total_amount': o.total_amount,
            'status': o.status,
            'order_date': o.order_date.strftime('%Y-%m-%d'),
            'created_at': o.created_at.strftime('%Y-%m-%d %H:%M')
        } for o in orders.items],
        'total': orders.total,
        'pages': orders.pages
    })

# ==================== 库存接口 ====================

@app.route('/api/inventory', methods=['GET'])
@token_required
def get_inventory(current_user):
    """获取库存列表"""
    warehouse_id = request.args.get('warehouse_id', type=int)
    low_stock = request.args.get('low_stock', type=bool)
    
    query = Inventory.query.join(Item).join(Warehouse)
    
    if warehouse_id:
        query = query.filter_by(warehouse_id=warehouse_id)
    if low_stock:
        query = query.join(Item).filter(Inventory.quantity < Item.min_stock)
    
    items = query.all()
    
    return jsonify({
        'success': True,
        'data': [{
            'id': i.id,
            'item_name': Item.query.get(i.item_id).name,
            'item_code': Item.query.get(i.item_id).code,
            'warehouse_name': Warehouse.query.get(i.warehouse_id).name,
            'quantity': i.quantity,
            'unit': i.unit,
            'min_stock': Item.query.get(i.item_id).min_stock,
            'is_low': i.quantity < Item.query.get(i.item_id).min_stock
        } for i in items]
    })

# ==================== 统计接口 ====================

@app.route('/api/stats', methods=['GET'])
@token_required
def get_stats(current_user):
    """获取统计数据"""
    today = date.today()
    
    # 今日统计
    inbound_today = db.session.query(db.func.sum(InboundOrder.total_amount)).filter(
        InboundOrder.order_date == today
    ).scalar() or 0
    
    outbound_today = db.session.query(db.func.sum(OutboundOrder.total_amount)).filter(
        OutboundOrder.order_date == today
    ).scalar() or 0
    
    # 本月统计
    month_start = today.replace(day=1)
    inbound_month = db.session.query(db.func.sum(InboundOrder.total_amount)).filter(
        InboundOrder.order_date >= month_start
    ).scalar() or 0
    
    outbound_month = db.session.query(db.func.sum(OutboundOrder.total_amount)).filter(
        OutboundOrder.order_date >= month_start
    ).scalar() or 0
    
    # 库存统计
    total_items = Inventory.query.count()
    low_stock = db.session.query(Inventory).join(Item).filter(
        Inventory.quantity < Item.min_stock
    ).count()
    
    return jsonify({
        'success': True,
        'data': {
            'inbound_today': inbound_today,
            'outbound_today': outbound_today,
            'inbound_month': inbound_month,
            'outbound_month': outbound_month,
            'profit_today': outbound_today - inbound_today,
            'profit_month': outbound_month - inbound_month,
            'total_items': total_items,
            'low_stock_count': low_stock
        }
    })

# ==================== 初始化 ====================

def init_db():
    """初始化数据库和默认数据"""
    with app.app_context():
        db.create_all()
        
        # 检查是否有默认管理员
        if not User.query.filter_by(username='admin').first():
            admin = User(
                username='admin',
                password=hash_password('admin123'),
                real_name='系统管理员',
                role='admin'
            )
            db.session.add(admin)
            
            # 添加默认仓库
            warehouse = Warehouse(name='主仓库', code='WH001')
            db.session.add(warehouse)
            
            # 添加默认物品分类
            cat1 = ItemCategory(name='原材料')
            cat2 = ItemCategory(name='产品')
            cat3 = ItemCategory(name='辅料')
            db.session.add_all([cat1, cat2, cat3])
            
            db.session.commit()
            print("默认管理员账号：admin / admin123")
        
        print("数据库初始化完成!")

if __name__ == '__main__':
    init_db()
    print("=" * 60)
    print("工厂出入库管理系统 - 服务器端")
    print("=" * 60)
    print("访问地址：http://0.0.0.0:5000")
    print("API 文档：http://localhost:5000/api/docs")
    print("=" * 60)
    app.run(host='0.0.0.0', port=5000, debug=True)
