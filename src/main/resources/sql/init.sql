
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


DELETE FROM flyway_schema_history WHERE version = '10';



SELECT
    nz.name AS nomenclature_name,
    nz.article AS nomenclature_article,
    nz.code AS nomenclature_code,
    nz.measurement_unit AS nomenclature_unit,
    i.quantity AS inventory_quantity,
    wc.serial_number AS container_serial,
    wz.name AS warehouse_zone_name,
    w.location AS warehouse_location
FROM
    t_inventory i
JOIN t_nomenclature nz ON i.nomenclature_id = nz.id
JOIN t_warehouse_zones wz ON i.warehouse_zone_id = wz.id
JOIN t_warehouses w ON wz.warehouse_id = w.id
LEFT JOIN t_warehouse_containers wc ON i.container_serial = wc.serial_number;



SELECT
    w.id AS warehouse_id,
    w.name AS warehouse_name,
    w.location AS warehouse_location,
    z.id AS zone_id,
    z.name AS zone_name,
    c.serial_number AS container_serial,
    n.id AS nomenclature_id,
    n.name AS nomenclature_name,
    n.measurement_unit,
    i.quantity
FROM
    t_warehouses w
JOIN
    t_warehouse_zones z ON z.warehouse_id = w.id
LEFT JOIN
    t_warehouse_containers c ON c.warehouse_zone_id = z.id
LEFT JOIN
    t_inventory i ON i.container_serial = c.serial_number
LEFT JOIN
    t_nomenclature n ON n.id = i.nomenclature_id
WHERE
    w.id = 1
ORDER BY
    z.id, c.serial_number, n.id;



select
wz1_0.id,
wz1_0.created_at,
wz1_0.created_by,
wz1_0.name,
p1_0.id,
p1_0.created_at,
p1_0.created_by,
p1_0.name,
p1_0.parent_id,
p1_0.updated_at,
p1_0.updated_by,
p1_0.warehouse_id,
w1_0.id,
w1_0.created_at,
w1_0.location,
w1_0.name,
w1_0.updated_at,
wz1_0.updated_at,
wz1_0.updated_by,
wz1_0.warehouse_id,
w2_0.id,
w2_0.created_at,
w2_0.location,
w2_0.name,
w2_0.updated_at
from t_warehouse_zones wz1_0
left join t_warehouse_zones p1_0
on p1_0.id=wz1_0.parent_id
left join t_warehouses w1_0
on w1_0.id=p1_0.warehouse_id
join t_warehouses w2_0
on w2_0.id=wz1_0.warehouse_id
where wz1_0.id=1