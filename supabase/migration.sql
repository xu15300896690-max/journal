-- ============================================
-- 工厂出入库管理系统 - Supabase 数据库迁移
-- Factory Inventory Management System
-- ============================================

-- 启用 UUID 扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- 1. 基础数据表
-- ============================================

-- 用户资料扩展表 (Supabase Auth 自动管理 auth.users)
CREATE TABLE profiles (
    id UUID REFERENCES auth.users PRIMARY KEY,
    username TEXT UNIQUE,
    real_name TEXT,
    phone TEXT,
    role TEXT DEFAULT 'operator' CHECK (role IN ('admin', 'manager', 'operator', 'viewer')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 供应商
CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    contact TEXT,
    phone TEXT,
    address TEXT,
    note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 客户
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    contact TEXT,
    phone TEXT,
    address TEXT,
    note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 物品分类
CREATE TABLE item_categories (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    parent_id BIGINT REFERENCES item_categories(id),
    sort_order INT DEFAULT 0
);

-- 物品
CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    code TEXT UNIQUE,
    category_id BIGINT REFERENCES item_categories(id),
    spec TEXT,
    unit TEXT DEFAULT '件',
    min_stock INT DEFAULT 0,
    max_stock INT,
    note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 仓库
CREATE TABLE warehouses (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    code TEXT UNIQUE,
    address TEXT,
    manager TEXT,
    phone TEXT,
    note TEXT
);

-- 库位
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    warehouse_id BIGINT REFERENCES warehouses(id),
    code TEXT NOT NULL,
    name TEXT,
    zone TEXT
);

-- ============================================
-- 2. 业务表
-- ============================================

-- 入库单
CREATE TABLE inbound_orders (
    id BIGSERIAL PRIMARY KEY,
    order_no TEXT UNIQUE NOT NULL,
    warehouse_id BIGINT REFERENCES warehouses(id),
    supplier_id BIGINT REFERENCES suppliers(id),
    plate_number TEXT,
    driver_name TEXT,
    driver_phone TEXT,
    total_amount DECIMAL(10,2) DEFAULT 0,
    status TEXT DEFAULT 'pending' CHECK (status IN ('pending', 'processing', 'completed', 'cancelled')),
    operator_id UUID REFERENCES profiles(id),
    order_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    completed_at TIMESTAMP WITH TIME ZONE,
    note TEXT
);

-- 入库明细
CREATE TABLE inbound_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES inbound_orders(id) ON DELETE CASCADE,
    item_id BIGINT REFERENCES items(id),
    location_id BIGINT REFERENCES locations(id),
    quantity INT NOT NULL,
    unit TEXT,
    gross_weight DECIMAL(10,2),
    tare_weight DECIMAL(10,2),
    net_weight DECIMAL(10,2),
    weight_unit TEXT DEFAULT '吨',
    unit_price DECIMAL(10,2) DEFAULT 0,
    amount DECIMAL(10,2) DEFAULT 0,
    batch_no TEXT,
    production_date DATE,
    expiry_date DATE,
    note TEXT
);

-- 出库单
CREATE TABLE outbound_orders (
    id BIGSERIAL PRIMARY KEY,
    order_no TEXT UNIQUE NOT NULL,
    warehouse_id BIGINT REFERENCES warehouses(id),
    customer_id BIGINT REFERENCES customers(id),
    plate_number TEXT,
    driver_name TEXT,
    driver_phone TEXT,
    total_amount DECIMAL(10,2) DEFAULT 0,
    status TEXT DEFAULT 'pending' CHECK (status IN ('pending', 'processing', 'completed', 'cancelled')),
    operator_id UUID REFERENCES profiles(id),
    order_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    completed_at TIMESTAMP WITH TIME ZONE,
    note TEXT
);

-- 出库明细
CREATE TABLE outbound_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES outbound_orders(id) ON DELETE CASCADE,
    item_id BIGINT REFERENCES items(id),
    location_id BIGINT REFERENCES locations(id),
    quantity INT NOT NULL,
    unit TEXT,
    unit_price DECIMAL(10,2) DEFAULT 0,
    amount DECIMAL(10,2) DEFAULT 0,
    batch_no TEXT,
    note TEXT
);

-- 库存
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT REFERENCES items(id),
    warehouse_id BIGINT REFERENCES warehouses(id),
    location_id BIGINT REFERENCES locations(id),
    quantity INT DEFAULT 0,
    unit TEXT,
    batch_no TEXT,
    last_inbound TIMESTAMP WITH TIME ZONE,
    last_outbound TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(item_id, warehouse_id, location_id, batch_no)
);

-- 记账
CREATE TABLE accounting (
    id BIGSERIAL PRIMARY KEY,
    type TEXT NOT NULL CHECK (type IN ('income', 'expense')),
    category TEXT,
    amount DECIMAL(10,2) NOT NULL,
    description TEXT,
    related_type TEXT,
    related_id BIGINT,
    operator_id UUID REFERENCES profiles(id),
    date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- ============================================
-- 3. 索引
-- ============================================

CREATE INDEX idx_inbound_orders_status ON inbound_orders(status);
CREATE INDEX idx_inbound_orders_date ON inbound_orders(order_date);
CREATE INDEX idx_outbound_orders_status ON outbound_orders(status);
CREATE INDEX idx_outbound_orders_date ON outbound_orders(order_date);
CREATE INDEX idx_inventory_item ON inventory(item_id);
CREATE INDEX idx_inventory_warehouse ON inventory(warehouse_id);
CREATE INDEX idx_accounting_date ON accounting(date);

-- ============================================
-- 4. 数据库函数
-- ============================================

-- 生成单号函数
CREATE OR REPLACE FUNCTION generate_order_no(prefix TEXT)
RETURNS TEXT AS $$
BEGIN
    RETURN prefix || TO_CHAR(NOW(), 'YYYYMMDDHH24MISS');
END;
$$ LANGUAGE plpgsql;

-- 创建入库单函数（包含库存更新）
CREATE OR REPLACE FUNCTION create_inbound_order(
    p_warehouse_id BIGINT,
    p_supplier_id BIGINT,
    p_plate_number TEXT,
    p_driver_name TEXT,
    p_driver_phone TEXT,
    p_items JSONB,
    p_note TEXT
)
RETURNS TABLE(order_id BIGINT, order_no TEXT) AS $$
DECLARE
    v_order_id BIGINT;
    v_order_no TEXT;
    v_item JSONB;
    v_total_amount DECIMAL := 0;
BEGIN
    -- 生成单号
    v_order_no := generate_order_no('IN');
    
    -- 创建入库单
    INSERT INTO inbound_orders (
        order_no, warehouse_id, supplier_id,
        plate_number, driver_name, driver_phone,
        note, status, order_date
    ) VALUES (
        v_order_no, p_warehouse_id, p_supplier_id,
        p_plate_number, p_driver_name, p_driver_phone,
        p_note, 'completed', CURRENT_DATE
    ) RETURNING id INTO v_order_id;
    
    -- 添加入库明细并更新库存
    FOR v_item IN SELECT * FROM jsonb_array_elements(p_items)
    LOOP
        DECLARE
            v_item_id BIGINT := (v_item->>'item_id')::BIGINT;
            v_location_id BIGINT := (v_item->>'location_id')::BIGINT;
            v_quantity INT := (v_item->>'quantity')::INT;
            v_unit TEXT := v_item->>'unit';
            v_unit_price DECIMAL := COALESCE((v_item->>'unit_price')::DECIMAL, 0);
            v_amount DECIMAL := v_quantity * v_unit_price;
            v_batch_no TEXT := v_item->>'batch_no';
        BEGIN
            -- 添加入库明细
            INSERT INTO inbound_items (
                order_id, item_id, location_id,
                quantity, unit, unit_price, amount,
                batch_no
            ) VALUES (
                v_order_id, v_item_id, v_location_id,
                v_quantity, v_unit, v_unit_price, v_amount,
                v_batch_no
            );
            
            v_total_amount := v_total_amount + v_amount;
            
            -- 更新库存
            INSERT INTO inventory (
                item_id, warehouse_id, location_id,
                quantity, unit, batch_no, last_inbound
            ) VALUES (
                v_item_id, p_warehouse_id, v_location_id,
                v_quantity, v_unit, v_batch_no, NOW()
            )
            ON CONFLICT (item_id, warehouse_id, location_id, COALESCE(batch_no, ''))
            DO UPDATE SET
                quantity = inventory.quantity + v_quantity,
                last_inbound = NOW(),
                updated_at = NOW();
        END;
    END LOOP;
    
    -- 更新入库单总金额
    UPDATE inbound_orders
    SET total_amount = v_total_amount
    WHERE id = v_order_id;
    
    -- 添加入库记账
    INSERT INTO accounting (
        type, category, amount,
        description, related_type, related_id
    ) VALUES (
        'expense', '采购', v_total_amount,
        '入库单 ' || v_order_no, 'inbound', v_order_id
    );
    
    RETURN QUERY SELECT v_order_id, v_order_no;
END;
$$ LANGUAGE plpgsql;

-- 创建出库单函数（包含库存扣减）
CREATE OR REPLACE FUNCTION create_outbound_order(
    p_warehouse_id BIGINT,
    p_customer_id BIGINT,
    p_plate_number TEXT,
    p_driver_name TEXT,
    p_driver_phone TEXT,
    p_items JSONB,
    p_note TEXT
)
RETURNS TABLE(order_id BIGINT, order_no TEXT, success BOOLEAN, message TEXT) AS $$
DECLARE
    v_order_id BIGINT;
    v_order_no TEXT;
    v_item JSONB;
    v_total_amount DECIMAL := 0;
    v_inventory RECORD;
BEGIN
    -- 检查库存
    FOR v_item IN SELECT * FROM jsonb_array_elements(p_items)
    LOOP
        DECLARE
            v_item_id BIGINT := (v_item->>'item_id')::BIGINT;
            v_quantity INT := (v_item->>'quantity')::INT;
        BEGIN
            SELECT * INTO v_inventory
            FROM inventory
            WHERE item_id = v_item_id AND warehouse_id = p_warehouse_id
            LIMIT 1;
            
            IF v_inventory IS NULL OR v_inventory.quantity < v_quantity THEN
                RETURN QUERY SELECT 0::BIGINT, ''::TEXT, FALSE, 
                    '物品 ID ' || v_item_id || ' 库存不足';
                RETURN;
            END IF;
        END;
    END LOOP;
    
    -- 生成单号
    v_order_no := generate_order_no('OUT');
    
    -- 创建出库单
    INSERT INTO outbound_orders (
        order_no, warehouse_id, customer_id,
        plate_number, driver_name, driver_phone,
        note, status, order_date
    ) VALUES (
        v_order_no, p_warehouse_id, p_customer_id,
        p_plate_number, p_driver_name, p_driver_phone,
        p_note, 'completed', CURRENT_DATE
    ) RETURNING id INTO v_order_id;
    
    -- 添加出库明细并扣减库存
    FOR v_item IN SELECT * FROM jsonb_array_elements(p_items)
    LOOP
        DECLARE
            v_item_id BIGINT := (v_item->>'item_id')::BIGINT;
            v_location_id BIGINT := (v_item->>'location_id')::BIGINT;
            v_quantity INT := (v_item->>'quantity')::INT;
            v_unit TEXT := v_item->>'unit';
            v_unit_price DECIMAL := COALESCE((v_item->>'unit_price')::DECIMAL, 0);
            v_amount DECIMAL := v_quantity * v_unit_price;
            v_batch_no TEXT := v_item->>'batch_no';
        BEGIN
            -- 添加出库明细
            INSERT INTO outbound_items (
                order_id, item_id, location_id,
                quantity, unit, unit_price, amount,
                batch_no
            ) VALUES (
                v_order_id, v_item_id, v_location_id,
                v_quantity, v_unit, v_unit_price, v_amount,
                v_batch_no
            );
            
            v_total_amount := v_total_amount + v_amount;
            
            -- 扣减库存
            UPDATE inventory
            SET quantity = quantity - v_quantity,
                last_outbound = NOW(),
                updated_at = NOW()
            WHERE item_id = v_item_id 
              AND warehouse_id = p_warehouse_id;
        END;
    END LOOP;
    
    -- 更新出库单总金额
    UPDATE outbound_orders
    SET total_amount = v_total_amount
    WHERE id = v_order_id;
    
    -- 添加出库记账
    INSERT INTO accounting (
        type, category, amount,
        description, related_type, related_id
    ) VALUES (
        'income', '销售', v_total_amount,
        '出库单 ' || v_order_no, 'outbound', v_order_id
    );
    
    RETURN QUERY SELECT v_order_id, v_order_no, TRUE, '成功';
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- 5. Row Level Security (RLS)
-- ============================================

-- 启用 RLS
ALTER TABLE suppliers ENABLE ROW LEVEL SECURITY;
ALTER TABLE customers ENABLE ROW LEVEL SECURITY;
ALTER TABLE items ENABLE ROW LEVEL SECURITY;
ALTER TABLE warehouses ENABLE ROW LEVEL SECURITY;
ALTER TABLE locations ENABLE ROW LEVEL SECURITY;
ALTER TABLE inbound_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE inbound_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE outbound_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE outbound_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE inventory ENABLE ROW LEVEL SECURITY;
ALTER TABLE accounting ENABLE ROW LEVEL SECURITY;
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;

-- 创建策略：所有认证用户可读取基础数据
CREATE POLICY "Allow authenticated users to read suppliers"
    ON suppliers FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Allow authenticated users to read customers"
    ON customers FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Allow authenticated users to read items"
    ON items FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Allow authenticated users to read warehouses"
    ON warehouses FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Allow authenticated users to read locations"
    ON locations FOR SELECT
    TO authenticated
    USING (true);

-- 入库单：认证用户可读，操作员及以上可写
CREATE POLICY "Allow authenticated users to read inbound orders"
    ON inbound_orders FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Allow operators to insert inbound orders"
    ON inbound_orders FOR INSERT
    TO authenticated
    WITH CHECK (true);

-- 出库单：认证用户可读，操作员及以上可写
CREATE POLICY "Allow authenticated users to read outbound orders"
    ON outbound_orders FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Allow operators to insert outbound orders"
    ON outbound_orders FOR INSERT
    TO authenticated
    WITH CHECK (true);

-- 库存：认证用户可读
CREATE POLICY "Allow authenticated users to read inventory"
    ON inventory FOR SELECT
    TO authenticated
    USING (true);

-- 记账：认证用户可读
CREATE POLICY "Allow authenticated users to read accounting"
    ON accounting FOR SELECT
    TO authenticated
    USING (true);

-- 用户资料：自己可读写自己的资料
CREATE POLICY "Users can view own profile"
    ON profiles FOR SELECT
    TO authenticated
    USING (auth.uid() = id);

CREATE POLICY "Users can update own profile"
    ON profiles FOR UPDATE
    TO authenticated
    USING (auth.uid() = id);

-- ============================================
-- 6. 触发器（自动更新 updated_at）
-- ============================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_profiles_updated_at
    BEFORE UPDATE ON profiles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_inventory_updated_at
    BEFORE UPDATE ON inventory
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 7. 初始化数据
-- ============================================

-- 默认仓库
INSERT INTO warehouses (name, code) VALUES ('主仓库', 'WH001');

-- 默认物品分类
INSERT INTO item_categories (name, sort_order) VALUES 
    ('原材料', 1),
    ('产品', 2),
    ('辅料', 3),
    ('包装材料', 4);

-- 默认供应商
INSERT INTO suppliers (name, contact, phone, address) VALUES
    ('上海钢铁厂', '张三', '13800138000', '上海市'),
    ('五金建材公司', '李四', '13900139000', '苏州市'),
    ('原材料供应商', '王五', '13700137000', '杭州市');

-- 默认客户
INSERT INTO customers (name, contact, phone, address) VALUES
    ('某建筑公司', '赵六', '13600136000', '南京市'),
    ('贸易公司', '钱七', '13500135000', '武汉市'),
    ('制造企业', '孙八', '13400134000', '成都市');

-- ============================================
-- 完成提示
-- ============================================

DO $$
BEGIN
    RAISE NOTICE '✅ 数据库迁移完成!';
    RAISE NOTICE '📦 表数量：% ', (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public');
    RAISE NOTICE '🔧 函数数量：% ', (SELECT COUNT(*) FROM pg_proc WHERE pronamespace = 'public'::regnamespace);
END $$;
