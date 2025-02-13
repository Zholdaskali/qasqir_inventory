CREATE TABLE IF NOT EXISTS t_inventory_audits
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор инвентаризации
    warehouse_id BIGINT NOT NULL, -- ID склада
    audit_date DATE NOT NULL, -- Дата проведения инвентаризации
    status VARCHAR(50) NOT NULL, -- Статус инвентаризации (например, "в процессе", "завершена")
    created_by BIGINT, -- ID пользователя, создавшего инвентаризацию
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время последнего изменения записи
    FOREIGN KEY (warehouse_id) REFERENCES t_warehouses (id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES t_users (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS t_inventory_audit_results
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор записи
    audit_id BIGINT NOT NULL, -- ID инвентаризации
    nomenclature_id BIGINT NOT NULL, -- ID товара
    warehouse_zone_id BIGINT NOT NULL, -- ID зоны склада
    expected_quantity NUMERIC(15, 3) NOT NULL, -- Ожидаемое количество (по данным системы)
    actual_quantity NUMERIC(15, 3) NOT NULL, -- Фактическое количество (по результатам инвентаризации)
    discrepancy NUMERIC(15, 3) NOT NULL, -- Расхождение (actual_quantity - expected_quantity)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    FOREIGN KEY (audit_id) REFERENCES t_inventory_audits (id) ON DELETE CASCADE,
    FOREIGN KEY (nomenclature_id) REFERENCES t_nomenclature (id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_zone_id) REFERENCES t_warehouse_zones (id) ON DELETE CASCADE
);