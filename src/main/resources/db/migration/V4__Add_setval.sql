SELECT setval(pg_get_serial_sequence('t_users', 'id'), 6, false);

SELECT setval(pg_get_serial_sequence('t_user_roles', 'id'), 6, false);
