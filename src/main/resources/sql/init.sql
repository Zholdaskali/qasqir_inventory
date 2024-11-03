--##############################  INIT DATABASE ##############################
--DROP DATABASE IF EXISTS qasqir_inventory;
--CREATE DATABASE qasqir_inventory;

\connect qasqir_inventory;

CREATE TABLE t_organizations
(
    id                  BIGSERIAL       NOT NULL,
    organization_name   VARCHAR(50)     NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE t_users
(
    id                  BIGSERIAL       NOT NULL,
    user_name           VARCHAR(20)     NOT NULL,
    password            VARCHAR(256)    NOT NULL,
    email               VARCHAR(70)     NOT NULL,
    email_verified      BOOLEAN         NOT NULL,
    organization_id     BIGINT          NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (organization_id) REFERENCES t_organizations (id) ON DELETE CASCADE
);

CREATE TABLE t_sessions
(
    id          BIGSERIAL               NOT NULL,
    expiration  TIMESTAMP               NOT NULL,
    token       VARCHAR(256)            NOT NULL,
    user_id     BIGINT                  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE
);

CREATE TABLE t_roles
(
    id          BIGSERIAL               NOT NULL,
    role_name   VARCHAR(20)             NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE t_user_roles
(
    id          BIGSERIAL               NOT NULL,
    user_id     BIGINT	              	NOT NULL,
    role_id     BIGINT             		NOT NULL,
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES t_roles (id) ON DELETE CASCADE
);

CREATE TABLE t_mail_verifications
(
    id              BIGSERIAL           NOT NULL,
    email           VARCHAR(70)         NOT NULL,
    code            VARCHAR(6)          NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE t_invites
(
    id              BIGSERIAL           NOT NULL,
    token           VARCHAR(256)        NOT NULL,
    link            VARCHAR(255)        NOT NULL,
    date_create     TIMESTAMP           NOT NULL,
    expiration      TIMESTAMP           NOT NULL,
    user_id         BIGINT              NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE
);

CREATE TABLE t_organization_admins
(
    id                  BIGSERIAL       NOT NULL,
    user_id             BIGINT          NOT NULL,
    organization_id     BIGINT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES t_organizations (id) ON DELETE CASCADE
);

--############################## INDEX ##############################




--############################## Test data ##############################
INSERT INTO t_organizations (id, organization_name)
VALUES
    (1, 'QASQIR'),
    (2, 'TEST');


INSERT INTO t_roles (id, role_name)
VALUES
    (1, 'employee'),
    (2, 'company_admin'),
    (3, 'super_admin');

INSERT INTO t_users (id, user_name, password, email, email_verified)
VALUES
    (1, 'SuperAdmin1', '$2a$12$W3qpaw./1DZy/t3elNdtjeo.rttSOnPWywU.8tdOuWgTwNGIWvmhq', 'zholdaskalierkebulan@gmail.com', true),
    (2, 'SuperAdmin2', '$2a$12$TxN7SBjj.MbnlQz9mnDA2e8dbEq7bZsPH5P7cNBKlukBnq3VukVFW', 'erkebulanzholdaskali@gmail.com', true);

DELETE FROM t_users WHERE id=2;

INSERT INTO t_user_roles (id, user_id, role_id)
VALUES
    (1, 1, 3),
    (2, 2, 3);

--############################## SELECT ##############################
SELECT * FROM t_organizations;

SELECT * FROM t_roles;

SELECT * FROM t_invites;

SELECT * FROM t_mail_verifications;

SELECT * FROM t_user_roles;

SELECT * FROM t_sessions;

SELECT * FROM t_users;

SELECT * FROM t_organization_admins;

--  Вывод всех действуюших приглашенных пользователей
SELECT i.id, u.user_name AS userName, u.email
        FROM t_invites i
        JOIN t_users u ON i.user_id = u.id

select s1_0.id,s1_0.expiration,s1_0.token,s1_0.user_id from t_sessions s1_0 where s1_0.token= 'lmGFz9z4RUgqt7i87kxeP5iaIRvI7NtL8iYkS5Xob7bXcBQGk39o71znG6E2M4SfvIkBRsToERAqz4AnUAOz2xcf2nBf186UE9O34wMdpzSnXYfC7RoL37rwCTQHqXri'

DELETE FROM t_users WHERE id = 2;

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

SELECT * FROM vw_user_profile

SELECT * FROM flyway_schema_history