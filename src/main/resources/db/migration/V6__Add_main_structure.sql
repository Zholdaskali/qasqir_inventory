-- Таблица для хранения складов
CREATE TABLE IF NOT EXISTS t_warehouses
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор склада
    name VARCHAR(255) NOT NULL, -- Название склада
    location VARCHAR(255), -- Местоположение склада
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Дата и время последнего изменения записи
);

-- Таблица для хранения данных о зонах склада
CREATE TABLE IF NOT EXISTS t_warehouse_zones
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор зоны
    warehouse_id BIGINT NOT NULL, -- ID склада, к которому относится зона
    name VARCHAR(255) NOT NULL, -- Название зоны
    parent_id BIGINT, -- Ссылка на родительскую зону
    created_by BIGINT, -- ID пользователя, создавшего зону
    updated_by BIGINT, -- ID пользователя, обновившего зону
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время последнего изменения записи
    FOREIGN KEY (warehouse_id) REFERENCES t_warehouses (id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES t_warehouse_zones (id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES t_users (id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES t_users (id) ON DELETE SET NULL
);

-- Таблица для хранения категорий товаров
CREATE TABLE IF NOT EXISTS t_categories
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор категории
    name VARCHAR(255) NOT NULL, -- Название категории
    created_by BIGINT, -- ID пользователя, создавшего категорию
    updated_by BIGINT, -- ID пользователя, обновившего категорию
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время последнего изменения записи
    FOREIGN KEY (created_by) REFERENCES t_users (id) ON DELETE SET NULL, -- Связь с пользователем
    FOREIGN KEY (updated_by) REFERENCES t_users (id) ON DELETE SET NULL -- Связь с пользователем
);

-- Таблица для хранения информации о товарах (номенклатура)
CREATE TABLE IF NOT EXISTS t_nomenclature
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор товара
    name VARCHAR(255) NOT NULL, -- Полное наименование товара
    article VARCHAR(100), -- Артикул товара
    code VARCHAR(100) NOT NULL UNIQUE, -- Уникальный код товара
    type VARCHAR(100) NOT NULL, -- Тип товара
    category_id BIGINT, -- ID категории товара
    measurement_unit VARCHAR(50) NOT NULL, -- Единица измерения товара
    tnved_code VARCHAR(20), -- Код ТНВЭД
    created_by BIGINT, -- ID пользователя, создавшего запись
    updated_by BIGINT, -- ID пользователя, обновившего запись
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время последнего изменения записи
    FOREIGN KEY (category_id) REFERENCES t_categories (id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES t_users (id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES t_users (id) ON DELETE SET NULL
);

-- Таблица для хранения данных о поставщиках
CREATE TABLE IF NOT EXISTS t_suppliers
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор поставщика
    name VARCHAR(255) NOT NULL, -- Название поставщика
    contact_info TEXT, -- Контактная информация поставщика
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Дата и время последнего изменения записи
);

-- Таблица для хранения данных о покупателях
CREATE TABLE IF NOT EXISTS t_customers
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор покупателя
    name VARCHAR(255) NOT NULL, -- Название покупателя
    contact_info TEXT, -- Контактная информация покупателя
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Дата и время последнего изменения записи
);

-- Таблица для хранения данных о документах (накладных, инвойсах и т.д.)
CREATE TABLE IF NOT EXISTS t_documents
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор документа
    document_type VARCHAR(50) NOT NULL, -- Тип документа (например, "накладная", "инвойс")
    document_number VARCHAR(100) NOT NULL, -- Номер документа
    document_date DATE NOT NULL, -- Дата документа
    supplier_id BIGINT, -- ID поставщика
    customer_id BIGINT, -- ID покупателя
    created_by BIGINT, -- ID пользователя, создавшего документ
    updated_by BIGINT, -- ID пользователя, обновившего документ
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время последнего изменения записи
    FOREIGN KEY (supplier_id) REFERENCES t_suppliers (id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES t_customers (id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES t_users (id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES t_users (id) ON DELETE SET NULL
);

-- Таблица для учета текущего состояния товаров на складе (инвентаризация)
CREATE TABLE IF NOT EXISTS t_inventory
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор записи
    nomenclature_id BIGINT NOT NULL, -- ID товара
    quantity NUMERIC(15, 3) DEFAULT 0, -- Текущее количество товара
    warehouse_zone_id BIGINT NOT NULL, -- ID зоны/места хранения
    container_serial VARCHAR(100), -- Серийный номер контейнера
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата последнего изменения записи
    FOREIGN KEY (nomenclature_id) REFERENCES t_nomenclature (id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_zone_id) REFERENCES t_warehouse_zones (id) ON DELETE CASCADE
);

-- Таблица для хранения данных о движении товаров на складе (операции)
CREATE TABLE IF NOT EXISTS t_transactions
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор транзакции
    transaction_type VARCHAR(50) NOT NULL, -- Тип операции
    document_id BIGINT NOT NULL, -- ID документа, связанного с транзакцией
    nomenclature_id BIGINT NOT NULL, -- ID товара
    quantity NUMERIC(15, 3) NOT NULL, -- Количество товара в операции
    date DATE NOT NULL, -- Дата операции
    created_by BIGINT, -- ID пользователя, создавшего транзакцию
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    FOREIGN KEY (document_id) REFERENCES t_documents (id) ON DELETE CASCADE,
    FOREIGN KEY (nomenclature_id) REFERENCES t_nomenclature (id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES t_users (id) ON DELETE SET NULL
);

-- Таблица для учета возвратов товаров
CREATE TABLE IF NOT EXISTS t_returns
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор возврата
    return_type VARCHAR(50) NOT NULL, -- Тип возврата
    related_document_id BIGINT, -- ID документа, связанного с возвратом
    nomenclature_id BIGINT NOT NULL, -- ID товара
    quantity NUMERIC(15, 3) NOT NULL, -- Количество возвращаемого товара
    reason TEXT, -- Причина возврата
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    FOREIGN KEY (related_document_id) REFERENCES t_documents (id) ON DELETE SET NULL,
    FOREIGN KEY (nomenclature_id) REFERENCES t_nomenclature (id) ON DELETE CASCADE
);

-- Таблица для хранения данных о контейнерах на складе
CREATE TABLE IF NOT EXISTS t_warehouse_containers
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор контейнера
    warehouse_zone_id BIGINT NOT NULL, -- ID зоны склада
    serial_number VARCHAR(100) NOT NULL, -- Серийный номер контейнера
    capacity NUMERIC(15, 3), -- Вместимость контейнера
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время последнего изменения записи
    FOREIGN KEY (warehouse_zone_id) REFERENCES t_warehouse_zones (id) ON DELETE CASCADE
);
