--##############################  INIT DATABASE ##############################

-- Удаляем таблицы, если они существуют
DROP TABLE IF EXISTS t_user_roles CASCADE;
DROP TABLE IF EXISTS t_sessions CASCADE;
DROP TABLE IF EXISTS t_mail_verifications;
DROP TABLE IF EXISTS t_invites;
DROP TABLE IF EXISTS t_organization_admins;
DROP TABLE IF EXISTS t_users CASCADE;
DROP TABLE IF EXISTS t_roles CASCADE;
DROP TABLE IF EXISTS t_images;

-- Создание таблиц
CREATE TABLE IF NOT EXISTS t_images (
    id                  BIGSERIAL NOT NULL,
    image_path          VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_users
(
    id                  BIGSERIAL       NOT NULL,
    user_name           VARCHAR(40)     NOT NULL,
    password            VARCHAR(256)    NOT NULL,
    email               VARCHAR(70)     NOT NULL,
    phone_number        VARCHAR(15)     NOT NULL,
    registration_date   TIMESTAMP       NOT NULL DEFAULT current_timestamp,
    email_verified      BOOLEAN         NOT NULL,
    image_id            BIGINT          NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (image_id) REFERENCES t_images(id) ON DELETE SET NULL, -- Убираем CASCADE
    UNIQUE (email, phone_number)
);

CREATE TABLE IF NOT EXISTS t_sessions
(
    id          BIGSERIAL               NOT NULL,
    expiration  TIMESTAMP               NOT NULL,
    token       VARCHAR(256)            NOT NULL,
    user_id     BIGINT                  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_roles
(
    id          BIGSERIAL               NOT NULL,
    role_name   VARCHAR(20)             NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_user_roles
(
    id          BIGSERIAL               NOT NULL,
    user_id     BIGINT                  NOT NULL,
    role_id     BIGINT                  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES t_roles (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_mail_verifications
(
    id              BIGSERIAL           NOT NULL,
    email           VARCHAR(70)         NOT NULL,
    code            VARCHAR(6)          NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS t_invites
(
    id              BIGSERIAL           NOT NULL,
    token           VARCHAR(255)        NOT NULL,
    link            VARCHAR(255)        NOT NULL,
    date_create     TIMESTAMP           NOT NULL,
    expiration      TIMESTAMP           NOT NULL,
    user_id         BIGINT              NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_exception_log
(
    id        SERIAL                NOT NULL,
    cause     VARCHAR(150)          NOT NULL,
    message   VARCHAR(2048)         NOT NULL,
    timestamp TIMESTAMP             NOT NULL DEFAULT current_timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_login_log
(
    id        SERIAL                NOT NULL,
    user_id   BIGINT                NOT NULL,
    timestamp TIMESTAMP             NOT NULL DEFAULT current_timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_action_log
(
    id        SERIAL                NOT NULL,
    user_id   BIGINT                NOT NULL,
    action    VARCHAR(250)          NOT NULL,
    endpoint  VARCHAR(250)          NOT NULL,
    timestamp TIMESTAMP             NOT NULL DEFAULT current_timestamp,
    PRIMARY KEY (id)
);

-- Оставляем представление как есть
CREATE OR REPLACE VIEW vw_user_profile AS
SELECT
    u.id AS user_id,
    u.user_name,
    u.email,
    u.phone_number,
    u.registration_date,
    u.email_verified,
    r.role_name
FROM t_users u
    LEFT JOIN t_user_roles ur ON u.id = ur.user_id
    LEFT JOIN t_roles r ON ur.role_id = r.id;


CREATE OR REPLACE VIEW vw_users_roles AS
SELECT
    u.id AS user_id,
    u.user_name AS user_name,
    u.email AS email,
    u.email_verified AS email_verified,
    r.id AS role_id,
    r.role_name AS role_name
FROM
    t_users u
JOIN
    t_user_roles ur ON u.id = ur.user_id
JOIN
    t_roles r ON ur.role_id = r.id;


CREATE TABLE IF NOT EXISTS t_organization (
    bin                 CHAR(12) NOT NULL, -- Уникальный бизнес-идентификационный номер
    organization_name   VARCHAR(255) NOT NULL, -- Название организации
    email               VARCHAR(254) NOT NULL, -- Email
    owner_name          VARCHAR(255) NOT NULL, -- Имя владельца
    phone_number        VARCHAR(15) NOT NULL, -- Номер телефона
    registration_date   TIMESTAMP NOT NULL DEFAULT current_timestamp, -- Дата регистрации
    website_link        VARCHAR(255) NOT NULL, -- Ссылка на сайт
    address             TEXT NOT NULL, -- Полный адрес
    image_id            BIGINT NULL, -- Идентификатор изображения
    PRIMARY KEY (bin), -- БИН используется как первичный ключ
    FOREIGN KEY (image_id) REFERENCES t_images(id) ON DELETE SET NULL,
    UNIQUE (email, phone_number) -- Уникальные ограничения
);


CREATE TABLE IF NOT EXISTS t_password_reset_tokens (
    id          SERIAL          NOT NULL,
    user_email  VARCHAR(255)    NOT NULL,
    token       VARCHAR(255)    NOT NULL,
    link        VARCHAR(255)    NOT NULL,
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    expires_at  TIMESTAMP       NOT NULL,
    PRIMARY KEY(id)
);


-- Таблица для хранения складов
CREATE TABLE IF NOT EXISTS t_warehouses
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор склада
    name VARCHAR(255) NOT NULL, -- Название склада
    location VARCHAR(255), -- Местоположение склада
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
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
    width DECIMAL(20,10),
    height DECIMAL(20,10),
    length DECIMAL(20,10),
    capacity DECIMAL(20,10),
    can_store_items BOOLEAN DEFAULT TRUE,
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
    width DECIMAL(20,10),
    height DECIMAL(20,10),
    length DECIMAL(20,10),
    volume DECIMAL(20,10),
    sync_date TIMESTAMP,
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
    status VARCHAR(25),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время последнего изменения записи
    FOREIGN KEY (supplier_id) REFERENCES t_suppliers (id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES t_customers (id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES t_users (id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES t_users (id) ON DELETE SET NULL
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
    width DECIMAL(20,10),
    height DECIMAL(20,10),
    length DECIMAL(20,10),
    FOREIGN KEY (warehouse_zone_id) REFERENCES t_warehouse_zones (id) ON DELETE CASCADE
);

-- Таблица для учета текущего состояния товаров на складе (инвентаризация)
CREATE TABLE IF NOT EXISTS t_inventory
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор записи
    nomenclature_id BIGINT NOT NULL, -- ID товара
    quantity NUMERIC(15, 3) DEFAULT 0, -- Текущее количество товара
    warehouse_zone_id BIGINT, -- ID зоны/места хранения
    warehouse_container_id BIGINT, -- Серийный номер контейнера
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата последнего изменения записи
    FOREIGN KEY (nomenclature_id) REFERENCES t_nomenclature (id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_zone_id) REFERENCES t_warehouse_zones (id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_container_id) REFERENCES t_warehouse_containers (id) ON DELETE CASCADE
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

CREATE TABLE t_documents_files (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,     -- оригинальное имя файла
    file_path VARCHAR(1024) NOT NULL,    -- путь к файлу в файловой системе
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (document_id) REFERENCES t_documents (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_inventory_audits_system (
    id BIGSERIAL PRIMARY KEY, -- Уникальный ID системной инвентаризации
    audit_date DATE NOT NULL, -- Дата начала общей инвентаризации
    status VARCHAR(50) NOT NULL, -- Статус ("в процессе", "завершена", и т.д.)
    created_by BIGINT, -- Кто инициировал
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES t_users (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS t_inventory_audits
(
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор инвентаризации
    inventory_audit_system_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL, -- ID склада
    audit_date DATE NOT NULL, -- Дата проведения инвентаризации
    status VARCHAR(50) NOT NULL, -- Статус инвентаризации (например, "в процессе", "завершена")
    created_by BIGINT, -- ID пользователя, создавшего инвентаризацию
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время последнего изменения записи
    FOREIGN KEY (warehouse_id) REFERENCES t_warehouses (id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_audit_system_id) REFERENCES t_inventory_audits_system (id) ON DELETE CASCADE,
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

CREATE TABLE t_ticket (
    id SERIAL NOT NULL,
    type VARCHAR(10) NOT NULL,
    status VARCHAR(10) NOT NULL,
    document_id BIGINT NOT NULL,
    create_by BIGINT NOT NULL,
    create_at TIMESTAMP NOT NULL,
    manager_id BIGINT,
    managed_at TIMESTAMP,
    comment TEXT,
    inventory_id BIGINT NOT NULL,
    quantity NUMERIC(15, 3) DEFAULT 0,
    FOREIGN KEY (document_id) REFERENCES t_documents(id),
    FOREIGN KEY (create_by) REFERENCES t_users(id),
    FOREIGN KEY (manager_id) REFERENCES t_users(id),
    FOREIGN KEY (inventory_id) REFERENCES t_inventory(id)
);

CREATE TABLE IF NOT EXISTS t_transaction_placements (
    id BIGSERIAL PRIMARY KEY, -- Уникальный идентификатор записи
    transaction_id BIGINT NOT NULL, -- ID транзакции
    warehouse_zone_id BIGINT, -- ID зоны склада
    warehouse_container_id BIGINT, -- ID контейнера (если применимо)
    quantity NUMERIC(15, 3) NOT NULL, -- Количество товара, размещённого в зоне/контейнере
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания записи
    placement_type VARCHAR(10) NOT NULL CHECK (placement_type IN ('IN', 'OUT')),
    FOREIGN KEY (transaction_id) REFERENCES t_transactions(id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_zone_id) REFERENCES t_warehouse_zones(id) ON DELETE SET NULL,
    FOREIGN KEY (warehouse_container_id) REFERENCES t_warehouse_containers(id) ON DELETE SET NULL
);

-- Индексы для таблицы t_warehouses
CREATE INDEX idx_warehouses_location ON t_warehouses(location);

-- Индексы для таблицы t_warehouse_zones
CREATE INDEX idx_warehouse_zones_warehouse_id ON t_warehouse_zones(warehouse_id);
CREATE INDEX idx_warehouse_zones_parent_id ON t_warehouse_zones(parent_id);
CREATE INDEX idx_warehouse_zones_created_by ON t_warehouse_zones(created_by);
CREATE INDEX idx_warehouse_zones_updated_by ON t_warehouse_zones(updated_by);

-- Индексы для таблицы t_categories
CREATE INDEX idx_categories_name ON t_categories(name);
CREATE INDEX idx_categories_created_by ON t_categories(created_by);
CREATE INDEX idx_categories_updated_by ON t_categories(updated_by);

-- Индексы для таблицы t_nomenclature
CREATE INDEX idx_nomenclature_article ON t_nomenclature(article);
CREATE INDEX idx_nomenclature_type ON t_nomenclature(type);
CREATE INDEX idx_nomenclature_category_id ON t_nomenclature(category_id);
CREATE INDEX idx_nomenclature_created_by ON t_nomenclature(created_by);
CREATE INDEX idx_nomenclature_updated_by ON t_nomenclature(updated_by);

-- Индексы для таблицы t_suppliers
CREATE INDEX idx_suppliers_name ON t_suppliers(name);

-- Индексы для таблицы t_customers
CREATE INDEX idx_customers_name ON t_customers(name);

-- Индексы для таблицы t_documents
CREATE INDEX idx_documents_document_type ON t_documents(document_type);
CREATE INDEX idx_documents_supplier_id ON t_documents(supplier_id);
CREATE INDEX idx_documents_customer_id ON t_documents(customer_id);
CREATE INDEX idx_documents_created_by ON t_documents(created_by);
CREATE INDEX idx_documents_updated_by ON t_documents(updated_by);

-- Индексы для таблицы t_inventory
CREATE INDEX idx_inventory_nomenclature_id ON t_inventory(nomenclature_id);
CREATE INDEX idx_inventory_warehouse_zone_id ON t_inventory(warehouse_zone_id);

-- Индексы для таблицы t_transactions
CREATE INDEX idx_transactions_transaction_type ON t_transactions(transaction_type);
CREATE INDEX idx_transactions_document_id ON t_transactions(document_id);
CREATE INDEX idx_transactions_nomenclature_id ON t_transactions(nomenclature_id);
CREATE INDEX idx_transactions_created_by ON t_transactions(created_by);

-- Индексы для таблицы t_returns
CREATE INDEX idx_returns_return_type ON t_returns(return_type);
CREATE INDEX idx_returns_related_document_id ON t_returns(related_document_id);
CREATE INDEX idx_returns_nomenclature_id ON t_returns(nomenclature_id);

-- Индексы для таблицы t_warehouse_containers
CREATE INDEX idx_warehouse_containers_serial_number ON t_warehouse_containers(serial_number);
CREATE INDEX idx_warehouse_containers_warehouse_zone_id ON t_warehouse_containers(warehouse_zone_id);







CREATE OR REPLACE FUNCTION update_can_store_items()
RETURNS TRIGGER AS $$
BEGIN
    -- Если у зоны есть дочерние зоны, она не может содержать товары
    IF EXISTS (SELECT 1 FROM t_warehouse_zones WHERE parent_id = NEW.id) THEN
        NEW.can_store_items = FALSE;
    ELSE
        NEW.can_store_items = TRUE;
    END IF;

    -- Если зона является дочерней (у нее есть parent_id), обновляем родительскую зону
    IF NEW.parent_id IS NOT NULL THEN
        UPDATE t_warehouse_zones
        SET can_store_items = FALSE
        WHERE id = NEW.parent_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_can_store_items
BEFORE INSERT OR UPDATE ON t_warehouse_zones
FOR EACH ROW
EXECUTE FUNCTION update_can_store_items();



INSERT INTO t_roles (role_name)
VALUES
    ('admin'),
    ('warehouse_manager'),
    ('storekeeper'),
    ('employee'),
    ('api_bitrix'),
    ('one_c');

INSERT INTO t_users (user_name, password, email, phone_number, registration_date, email_verified)
VALUES
    ('SuperAdmin1', '$2a$12$W3qpaw./1DZy/t3elNdtjeo.rttSOnPWywU.8tdOuWgTwNGIWvmhq', 'superAdmin1@gmail.com', '+77011112235', CURRENT_TIMESTAMP, false),
    ('SuperAdmin2', '$2a$12$TxN7SBjj.MbnlQz9mnDA2e8dbEq7bZsPH5P7cNBKlukBnq3VukVFW', 'erkebulanzholdaskali@gmail.com', '+77478708845', CURRENT_TIMESTAMP, false),
    ('Zhanserik Bazarov', '$2a$12$ah9LssaFaS7./i6ATtx6w.MNmHTUwmaWdnWpPlcyo/Lkv3onZFtOK', 'zhako.bazarov2@gmail.com', '+77011112233', CURRENT_TIMESTAMP, false),
    ('Erkebulan Zholdaskali', '$2a$12$euR2Nu0Z7cw9QFZ/LQWxm.zHWsiYcbvcYu824wp.V0odMmBUJ0r5O', 'zholdaskalierkebulan2@gmail.com', '+77011112234', CURRENT_TIMESTAMP, false),
    ('Bitrix User', '$2a$12$W3qpaw./1DZy/t3elNdtjeo.rttSOnPWywU.8tdOuWgTwNGIWvmhq', 'bitrix@gmail.com', '+111111111', CURRENT_TIMESTAMP, false),
    ('1C User', '$2a$12$nERGrULBduCMssuNWS.VleMLrPye3kJb65cjtzctLvK7Y7lRfkRza', '1CQasqirInventory@gmail.com', '+77011112230', CURRENT_TIMESTAMP, true);


INSERT INTO t_user_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 1),
    (3, 3),
    (3, 4),
    (4, 2),
    (4, 3),
    (4, 4),
    (5, 5),
    (6, 6)
ON CONFLICT DO NOTHING;  -- Избегаем ошибок при дублировании


INSERT INTO t_organization (
    bin, organization_name, email, owner_name, phone_number, website_link, address
) VALUES (
    '170640007696',
    'TOO Alioth',
    'office@alioth.kz',
    'Бастрыкин Олег Викторович',
    '+77086529460',
    'https://qasqir.kz',
    'г. Алматы, ул. Торгут Озала 161'
);



