INSERT INTO t_roles (id, role_name)
VALUES
    (1, 'employee'),
    (2, 'warehouse_admin'),
    (3, 'super_admin')
ON CONFLICT DO NOTHING;  -- Избегаем ошибок при дублировании
