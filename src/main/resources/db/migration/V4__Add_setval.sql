SELECT setval(pg_get_serial_sequence('t_users', 'id'), 5, false);

SELECT setval(pg_get_serial_sequence('t_user_roles', 'id'), 5, false);
