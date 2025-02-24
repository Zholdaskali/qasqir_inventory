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
