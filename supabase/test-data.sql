-- ============================================
-- Supabase 测试数据
-- 用于快速验证集成是否成功
-- ============================================

-- 检查默认数据是否已存在
DO $$
DECLARE
    supplier_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO supplier_count FROM suppliers;
    
    IF supplier_count = 0 THEN
        -- 添加测试供应商
        INSERT INTO suppliers (name, contact, phone, address) VALUES
        ('上海钢铁厂', '张三', '13800138000', '上海市浦东新区'),
        ('苏州五金公司', '李四', '13900139000', '苏州市工业园区'),
        ('杭州原材料供应商', '王五', '13700137000', '杭州市西湖区');
        
        RAISE NOTICE '✅ 已添加测试供应商';
    END IF;
END $$;

DO $$
DECLARE
    customer_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO customer_count FROM customers;
    
    IF customer_count = 0 THEN
        -- 添加测试客户
        INSERT INTO customers (name, contact, phone, address) VALUES
        ('南京建筑公司', '赵六', '13600136000', '南京市鼓楼区'),
        ('武汉贸易公司', '钱七', '13500135000', '武汉市武昌区'),
        ('成都制造企业', '孙八', '13400134000', '成都市高新区');
        
        RAISE NOTICE '✅ 已添加测试客户';
    END IF;
END $$;

DO $$
DECLARE
    item_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO item_count FROM items;
    
    IF item_count = 0 THEN
        -- 添加测试物品
        INSERT INTO items (name, code, spec, unit, min_stock) VALUES
        ('螺纹钢', 'STEEL-001', 'Φ20mm', '吨', 10),
        ('无缝钢管', 'PIPE-001', 'DN50', '米', 100),
        ('螺丝钉', 'SCREW-001', 'M10x50', '件', 500),
        ('螺母', 'NUT-001', 'M10', '件', 500),
        ('垫片', 'WASHER-001', 'Φ10', '件', 1000);
        
        RAISE NOTICE '✅ 已添加测试物品';
    END IF;
END $$;

-- 添加测试入库单
DO $$
DECLARE
    v_order_no TEXT;
    v_warehouse_id BIGINT;
    v_supplier_id BIGINT;
    v_item_id BIGINT;
BEGIN
    SELECT id INTO v_warehouse_id FROM warehouses WHERE code = 'WH001' LIMIT 1;
    SELECT id INTO v_supplier_id FROM suppliers LIMIT 1;
    SELECT id INTO v_item_id FROM items LIMIT 1;
    
    IF v_warehouse_id IS NOT NULL AND v_supplier_id IS NOT NULL AND v_item_id IS NOT NULL THEN
        -- 生成单号
        v_order_no := 'IN' || TO_CHAR(NOW(), 'YYYYMMDDHH24MISS');
        
        -- 创建入库单
        INSERT INTO inbound_orders (
            order_no, warehouse_id, supplier_id,
            plate_number, driver_name, driver_phone,
            total_amount, status, order_date
        ) VALUES (
            v_order_no, v_warehouse_id, v_supplier_id,
            '沪 A12345', '张师傅', '13800138000',
            50000.00, 'completed', CURRENT_DATE
        );
        
        RAISE NOTICE '✅ 已添加测试入库单：%', v_order_no;
    END IF;
END $$;

-- 添加测试出库单
DO $$
DECLARE
    v_order_no TEXT;
    v_warehouse_id BIGINT;
    v_customer_id BIGINT;
    v_item_id BIGINT;
BEGIN
    SELECT id INTO v_warehouse_id FROM warehouses WHERE code = 'WH001' LIMIT 1;
    SELECT id INTO v_customer_id FROM customers LIMIT 1;
    SELECT id INTO v_item_id FROM items LIMIT 1;
    
    IF v_warehouse_id IS NOT NULL AND v_customer_id IS NOT NULL AND v_item_id IS NOT NULL THEN
        -- 生成单号
        v_order_no := 'OUT' || TO_CHAR(NOW(), 'YYYYMMDDHH24MISS');
        
        -- 创建出库单
        INSERT INTO outbound_orders (
            order_no, warehouse_id, customer_id,
            plate_number, driver_name, driver_phone,
            total_amount, status, order_date
        ) VALUES (
            v_order_no, v_warehouse_id, v_customer_id,
            '沪 B67890', '李师傅', '13900139000',
            30000.00, 'completed', CURRENT_DATE
        );
        
        RAISE NOTICE '✅ 已添加测试出库单：%', v_order_no;
    END IF;
END $$;

-- 添加测试库存
DO $$
DECLARE
    v_warehouse_id BIGINT;
    v_item RECORD;
BEGIN
    SELECT id INTO v_warehouse_id FROM warehouses WHERE code = 'WH001' LIMIT 1;
    
    IF v_warehouse_id IS NOT NULL THEN
        FOR v_item IN SELECT id, min_stock FROM items
        LOOP
            INSERT INTO inventory (
                item_id, warehouse_id,
                quantity, unit,
                last_inbound, updated_at
            ) VALUES (
                v_item.id, v_warehouse_id,
                v_item.min_stock * 2, '件',
                NOW(), NOW()
            )
            ON CONFLICT (item_id, warehouse_id, COALESCE(batch_no, ''))
            DO NOTHING;
        END LOOP;
        
        RAISE NOTICE '✅ 已添加测试库存';
    END IF;
END $$;

-- 显示测试数据摘要
DO $$
DECLARE
    supplier_count INTEGER;
    customer_count INTEGER;
    item_count INTEGER;
    inbound_count INTEGER;
    outbound_count INTEGER;
    inventory_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO supplier_count FROM suppliers;
    SELECT COUNT(*) INTO customer_count FROM customers;
    SELECT COUNT(*) INTO item_count FROM items;
    SELECT COUNT(*) INTO inbound_count FROM inbound_orders;
    SELECT COUNT(*) INTO outbound_count FROM outbound_orders;
    SELECT COUNT(*) INTO inventory_count FROM inventory;
    
    RAISE NOTICE '================================';
    RAISE NOTICE '📊 测试数据摘要';
    RAISE NOTICE '================================';
    RAISE NOTICE '供应商：% 家', supplier_count;
    RAISE NOTICE '客户：% 家', customer_count;
    RAISE NOTICE '物品：% 种', item_count;
    RAISE NOTICE '入库单：% 张', inbound_count;
    RAISE NOTICE '出库单：% 张', outbound_count;
    RAISE NOTICE '库存记录：% 条', inventory_count;
    RAISE NOTICE '================================';
END $$;
