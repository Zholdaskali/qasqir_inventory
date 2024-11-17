CREATE OR REPLACE VIEW vw_users_roles AS
SELECT
    u.id AS user_id,
    u.user_name AS user_name,
    u.email AS email,
    u.email_verified AS email_verified,
    r.id AS role_id,
    r.role_name AS role_name
FROM
    t_users u
JOIN
    t_user_roles ur ON u.id = ur.user_id
JOIN
    t_roles r ON ur.role_id = r.id;
