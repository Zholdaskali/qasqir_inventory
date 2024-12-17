-- Изменяем таблицу t_inventory, чтобы добавить внешний ключ, который ссылается на t_warehouse_containers
ALTER TABLE t_inventory
ADD CONSTRAINT fk_inventory_container
FOREIGN KEY (container_serial) REFERENCES t_warehouse_containers (serial_number)
ON DELETE CASCADE;
