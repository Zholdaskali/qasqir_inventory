CREATE TABLE IF NOT EXISTS t_transaction_placements (
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор записи
    transaction_id BIGINT NOT NULL, -- ID транзакции
    warehouse_zone_id BIGINT, -- ID зоны склада
    warehouse_container_id BIGINT, -- ID контейнера (если применимо)
    quantity NUMERIC(15, 3) NOT NULL, -- Количество товара, размещённого в зоне/контейнере
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    FOREIGN KEY (transaction_id) REFERENCES t_transactions(id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_zone_id) REFERENCES t_warehouse_zones(id) ON DELETE SET NULL,
    FOREIGN KEY (warehouse_container_id) REFERENCES t_warehouse_containers(id) ON DELETE SET NULL
);