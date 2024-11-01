INSERT INTO t_users (id, user_name, password, email, email_verified)
VALUES
    (1, 'SuperAdmin', '$2a$12$E1R.iRmttYdYa/xxZ5pUieQUKwakH/TWcifUlnKWnFKEbjflNn3RC', 'zholdaskalierkebulan@gmail.com', true),
    (2, 'SuperAdmin2', '$2a$12$6gPGYq8J9xJg.e52MjrSGOi6on7YUPh39wBWo2xWcwMIBxJEOga0K', 'erkebulanzholdaskali@gmail.com', true)
ON CONFLICT DO NOTHING;  -- Избегаем ошибок при дублировании


INSERT INTO t_user_roles (id, user_id, role_id)
VALUES
    (1, 1, 3),
    (2, 2, 3)
ON CONFLICT DO NOTHING;  -- Избегаем ошибок при дублировании
