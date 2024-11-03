SELECT setval(pg_get_serial_sequence('t_users', 'id'), 3, false);

SELECT setval(pg_get_serial_sequence('t_user_roles', 'id'), 3, false);
