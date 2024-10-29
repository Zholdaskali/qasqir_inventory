--##############################  INIT DATABASE ##############################
--DROP DATABASE IF EXISTS qasqir_inventory;
--CREATE DATABASE qasqir_inventory;

\connect qasqir_inventory;

CREATE TABLE t_organizations
(
    id                  SERIAL          NOT NULL,
    organization_name   VARCHAR(50)     NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE t_users
(
    id                  SERIAL          NOT NULL,
    user_name           VARCHAR(20)     NOT NULL,
    password            VARCHAR(256)    NOT NULL,
    email               VARCHAR(70)     NOT NULL,
    email_verified      BOOLEAN         NOT NULL,
    organization_id     INT             NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (organization_id) REFERENCES t_organizations (id) ON DELETE CASCADE
);

CREATE TABLE t_sessions
(
    id          SERIAL                  NOT NULL,
    expiration  TIMESTAMP               NOT NULL,
    token       VARCHAR(256)            NOT NULL,
    user_id     INT                     NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE
);

CREATE TABLE t_roles
(
    id          SERIAL                  NOT NULL,
    role_name   VARCHAR(20)             NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE t_user_roles
(
    id          SERIAL                  NOT NULL,
    user_id     INT	              		NOT NULL,
    role_id     INT             		NOT NULL,
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES t_roles (id) ON DELETE CASCADE
);

CREATE TABLE t_mail_verifications
(
    id              SERIAL              NOT NULL,
    email           VARCHAR(70)         NOT NULL,
    code            VARCHAR(6)          NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE t_invites
(
    id              SERIAL              NOT NULL,
    token           VARCHAR(256)        NOT NULL,
    link            VARCHAR(255)        NOT NULL,
    date_create     TIMESTAMP           NOT NULL,
    expiration      TIMESTAMP           NOT NULL,
    user_id         INT                 NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE
);

CREATE TABLE t_organization_admins
(
    id                  SERIAL          NOT NULL,
    user_id             INT             NOT NULL,
    organization_id     INT             NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE,
    FOREIGN KEY (organization_id) REFERENCES t_organizations (id) ON DELETE CASCADE
);

--############################## INDEX ##############################




--############################## Test data ##############################
INSERT INTO t_organizations (id, organization_name)
VALUES
    (1, 'QASQIR');

INSERT INTO t_roles (id, role_name)
VALUES
    (1, 'employee'),
    (2, 'company_admin'),
    (3, 'super_admin');

INSERT INTO t_users(id, user_name, password, email, email_verified)
values
    (1, 'Zholdaskali', 'erkeerke', 'zholdaskalierkebulan@gmail.com', true);

INSERT INTO t_user_roles(id, user_id, role_id)
values
    (1, 1, 3);


SELECT i.id AS id, u.user_name AS userName, u.email AS email
        FROM t_invites i
                JOIN t_users u ON i.user_id = u.id


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

Select u1_0.id,u1_0.email,u1_0.email_verified,u1_0.organization_id,u1_0.password,u1_0.user_name from t_users u1_0 where u1_0.user_name='Zholdaskali'


