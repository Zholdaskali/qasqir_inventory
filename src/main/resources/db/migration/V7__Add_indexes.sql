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
CREATE INDEX idx_inventory_container_serial ON t_inventory(container_serial);

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