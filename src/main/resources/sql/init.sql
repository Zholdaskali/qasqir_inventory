--##############################  INIT DATABASE ##############################
--##############################  INIT DATABASE ##############################

-- Удаляем таблицы, если они существуют
DROP TABLE IF EXISTS t_user_roles CASCADE;
DROP TABLE IF EXISTS t_sessions CASCADE;
DROP TABLE IF EXISTS t_mail_verifications CASCADE;
DROP TABLE IF EXISTS t_invites CASCADE;
DROP TABLE IF EXISTS t_organization_admins CASCADE;
DROP TABLE IF EXISTS t_users CASCADE;
DROP TABLE IF EXISTS t_roles CASCADE;
DROP TABLE IF EXISTS t_organizations CASCADE;

-- Создание таблиц
CREATE TABLE IF NOT EXISTS t_users
(
    id                  BIGSERIAL       NOT NULL,
    user_name           VARCHAR(20)     NOT NULL,
    password            VARCHAR(256)    NOT NULL,
    email               VARCHAR(70)     NOT NULL,
    email_verified      BOOLEAN         NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_sessions
(
    id          BIGSERIAL               NOT NULL,
    expiration  TIMESTAMP               NOT NULL,
    token       VARCHAR(256)            NOT NULL,
    user_id     BIGINT                  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_roles
(
    id          BIGSERIAL               NOT NULL,
    role_name   VARCHAR(20)             NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_user_roles
(
    id          BIGSERIAL               NOT NULL,
    user_id     BIGINT                  NOT NULL,
    role_id     BIGINT                  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES t_roles (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS t_mail_verifications
(
    id              BIGSERIAL           NOT NULL,
    email           VARCHAR(70)         NOT NULL,
    code            VARCHAR(6)          NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS t_invites
(
    id              BIGSERIAL           NOT NULL,
    token           VARCHAR(255)        NOT NULL,
    link            VARCHAR(255)        NOT NULL,
    date_create     TIMESTAMP           NOT NULL,
    expiration      TIMESTAMP           NOT NULL,
    user_id         BIGINT              NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE
);

-- Оставляем представление как есть
CREATE OR REPLACE VIEW vw_user_profile AS
SELECT
    u.id AS user_id,
    u.user_name,
    u.email,
    o.organization_name,
    u.email_verified,
    r.role_name
FROM t_users u
    LEFT JOIN t_organizations o ON u.organization_id = o.id
    LEFT JOIN t_user_roles ur ON u.id = ur.user_id
    LEFT JOIN t_roles r ON ur.role_id = r.id;





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

select ll1_0.id,ll1_0.timestamp,ll1_0.user_id from t_login_log ll1_0 where ll1_0.timestamp between '2024-11-11' and '2024-11-13'
