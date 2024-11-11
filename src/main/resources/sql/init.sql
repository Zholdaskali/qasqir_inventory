--##############################  INIT DATABASE ##############################
--############################## SELECT ##############################
SELECT * FROM t_organizations;

SELECT * FROM t_roles;

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

SELECT * FROM vw_users_roles

--  Вывод всех действуюших приглашенных пользователей
SELECT i.id, u.user_name AS userName, u.email
        FROM t_invites i
        JOIN t_users u ON i.user_id = u.id


