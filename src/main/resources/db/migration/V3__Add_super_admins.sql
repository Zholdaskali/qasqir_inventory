INSERT INTO t_users (id, user_name, password, email, email_verified)
VALUES
    (1, 'SuperAdmin1', '$2a$12$W3qpaw./1DZy/t3elNdtjeo.rttSOnPWywU.8tdOuWgTwNGIWvmhq', 'zholdaskalierkebulan@gmail.com', true),
    (2, 'SuperAdmin2', '$2a$12$TxN7SBjj.MbnlQz9mnDA2e8dbEq7bZsPH5P7cNBKlukBnq3VukVFW', 'erkebulanzholdaskali@gmail.com', true);



INSERT INTO t_user_roles (id, user_id, role_id)
VALUES
    (1, 1, 3),
    (2, 2, 3)
ON CONFLICT DO NOTHING;  -- Избегаем ошибок при дублировании
