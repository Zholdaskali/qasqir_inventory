CREATE TRIGGER trg_update_can_store_items
BEFORE INSERT OR UPDATE ON t_warehouse_zones
FOR EACH ROW
EXECUTE FUNCTION update_can_store_items();