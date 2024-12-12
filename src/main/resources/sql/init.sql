
//        {
//            "userName": "superAdmin1@gmail.com",
//            "password": "TorgutOzalaqasqirAdminkz02"
//        }
//
//        {
//            "userName": "erkebulanzholdaskali@gmail.com",
//            "password": "ErkebulanAdmin0404"
//        }

//        {
//            "userName": "zhako.bazarov2@gmail.com",
//            "password": "Zhanserik"
//        }
//
//        {
//            "userName": "zholdaskalierkebulan@gmail.com",
//            "password": "Erkebulan"
//        }


--############################## SELECT ##############################
SELECT * FROM t_organizations;

SELECT * FROM t_password_reset_tokens

SELECT * FROM t_roles;

SELECT * FROM t_images;

SELECT * FROM t_invites;

SELECT * FROM t_mail_verifications;

SELECT * FROM t_user_roles;

SELECT * FROM t_sessions;

SELECT * FROM t_users;

SELECT * FROM t_organization_admins;

SELECT * FROM t_login_log

SELECT * FROM t_action_log

SELECT * FROM t_exception_log

SELECT * FROM flyway_schema_history

SELECT * FROM t_warehouse_zones

SELECT * FROM vw_users_roles

--  Вывод всех действуюших приглашенных пользователей
SELECT i.id, u.user_name AS userName, u.email
        FROM t_invites i
        JOIN t_users u ON i.user_id = u.id

select ll1_0.id,ll1_0.timestamp,ll1_0.user_id from t_login_log ll1_0 where ll1_0.timestamp between '2024-11-11' and '2024-11-13'


SELECT u.user_id, u.user_name, u.email, tu.phone_number, tu.registration_date, i.image_path, u.email_verified, u.role_name
FROM vw_users_roles u
JOIN t_users tu ON u.user_id = tu.id
LEFT JOIN t_images i ON tu.image_id = i.id
WHERE u.user_id = 1;

--DROP TABLE IF EXISTS t_organization CASCADE;
--
--CREATE TABLE IF NOT EXISTS t_organization (
--    id                  BIGSERIAL NOT NULL,
--    organization_name   VARCHAR(255) NOT NULL,
--    email               VARCHAR(70)     NOT NULL,
--    owner_name          VARCHAR(255)    NOT NULL,
--    phone_number        VARCHAR(15)     NOT NULL,
--    registration_date   TIMESTAMP       NOT NULL DEFAULT current_timestamp,
--    website_link        VARCHAR(255)    NOT NULL,
--    image_id            BIGINT          NULL,
--    PRIMARY KEY (id),
--    FOREIGN KEY (image_id) REFERENCES t_images(id) ON DELETE SET NULL, -- Убираем CASCADE
--    UNIQUE (email, phone_number)
--);
select i1_0.id,i1_0.date_create,i1_0.expiration,i1_0.link,i1_0.token,i1_0.user_id from t_invites i1_0 where i1_0.token='xIEb08LlJYxygtVaoljwPk7id4uq1KhkvanSK3xl3C2YxSrUEERUNV05GbelFT7KkXiz6BlFKMpciuLfQeTWpH3ADNMoaRqH1PJfRJ4Sl08vveH52sysl8gaZDV6VMgN'

INSERT INTO t_images (id, image_path)
    VALUES (1, 'https://avatars.yandex.net/get-music-content/6202531/b3b7a179.a.23563811-1/m1000x1000?webp=false')


UPDATE t_users
SET image_id = 1
WHERE id = 2;
