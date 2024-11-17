ALTER TABLE t_users
ADD COLUMN phone_number VARCHAR(15);


DROP VIEW vw_user_profile CASCADE;

CREATE OR REPLACE VIEW vw_user_profile AS
SELECT
    u.id AS user_id,
    u.user_name,
    u.email,
    u.phone_number,
    u.email_verified,
    r.role_name
FROM t_users u
    LEFT JOIN t_user_roles ur ON u.id = ur.user_id
    LEFT JOIN t_roles r ON ur.role_id = r.id;
