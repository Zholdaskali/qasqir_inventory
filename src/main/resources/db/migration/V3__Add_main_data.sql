INSERT INTO t_users (id, user_name, password, email, phone_number, registration_date, email_verified)
VALUES
    (1, 'SuperAdmin1', '$2a$12$W3qpaw./1DZy/t3elNdtjeo.rttSOnPWywU.8tdOuWgTwNGIWvmhq', 'superAdmin1@gmail.com', '+77011112235', CURRENT_TIMESTAMP, false),
    (2, 'SuperAdmin2', '$2a$12$TxN7SBjj.MbnlQz9mnDA2e8dbEq7bZsPH5P7cNBKlukBnq3VukVFW', 'erkebulanzholdaskali@gmail.com', '+77478708845', CURRENT_TIMESTAMP, false),
    (3, 'Zhanserik Bazarov', '$2a$12$ah9LssaFaS7./i6ATtx6w.MNmHTUwmaWdnWpPlcyo/Lkv3onZFtOK', 'zhako.bazarov2@gmail.com', '+77011112233', CURRENT_TIMESTAMP, false),
    (4, 'Erkebulan Zholdaskali', '$2a$12$euR2Nu0Z7cw9QFZ/LQWxm.zHWsiYcbvcYu824wp.V0odMmBUJ0r5O', 'zholdaskalierkebulan2@gmail.com', '+77011112234', CURRENT_TIMESTAMP, false),
    (5, 'Bitrix User', '$2a$12$W3qpaw./1DZy/t3elNdtjeo.rttSOnPWywU.8tdOuWgTwNGIWvmhq', 'test@gmail.com', '+111111111', CURRENT_TIMESTAMP, false);


INSERT INTO t_user_roles (id, user_id, role_id)
VALUES
    (1, 1, 1),
    (2, 2, 1),
    (3, 3, 2),
    (4, 4, 3),
    (5, 5, 4)
ON CONFLICT DO NOTHING;  -- Избегаем ошибок при дублировании


INSERT INTO t_organization (
    bin, organization_name, email, owner_name, phone_number, website_link, address
) VALUES (
    '170640007696',
    'TOO Alioth',
    'office@alioth.kz',
    'Бастрыкин Олег Викторович',
    '+77086529460',
    'https://qasqir.kz',
    'г. Алматы, ул. Торгут Озала 161'
);