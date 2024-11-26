INSERT INTO t_users (id, user_name, password, email, phone_number, registration_date, email_verified)
VALUES
    (1, 'SuperAdmin1', '$2a$12$W3qpaw./1DZy/t3elNdtjeo.rttSOnPWywU.8tdOuWgTwNGIWvmhq', 'superAdmin1@gmail.com', '+77011112235', CURRENT_TIMESTAMP, false),
    (2, 'SuperAdmin2', '$2a$12$TxN7SBjj.MbnlQz9mnDA2e8dbEq7bZsPH5P7cNBKlukBnq3VukVFW', 'erkebulanzholdaskali@gmail.com', '+77478708845', CURRENT_TIMESTAMP, false),
    (3, 'Zhanserik Bazarov', '$2a$12$ah9LssaFaS7./i6ATtx6w.MNmHTUwmaWdnWpPlcyo/Lkv3onZFtOK', 'zhako.bazarov2@gmail.com', '+77011112233', CURRENT_TIMESTAMP, false),
    (4, 'Erkebulan Zholdaskali', '$2a$12$euR2Nu0Z7cw9QFZ/LQWxm.zHWsiYcbvcYu824wp.V0odMmBUJ0r5O', 'zholdaskalierkebulan@gmail.com', '+77011112234', CURRENT_TIMESTAMP, false);



INSERT INTO t_user_roles (id, user_id, role_id)
VALUES
    (1, 1, 3),
    (2, 2, 3),
    (3, 3, 2),
    (4, 4, 1)
ON CONFLICT DO NOTHING;  -- Избегаем ошибок при дублировании
