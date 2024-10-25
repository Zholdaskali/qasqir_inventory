--##############################  INIT DATABASE ##############################

DROP DATABASE IF EXISTS qasqir_inventory;
CREATE DATABASE qasqir_inventory;

\connect qasqir_inventory;

CREATE TABLE t_organizations
(
    id                  SERIAL          NOT NULL,
    organization_name   VARCHAR(50)     NOT NULL,
    admin_mail          VARCHAR(50)     NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE t_users
(
    id                  SERIAL          NOT NULL,
    user_name           VARCHAR(20)     NOT NULL,
    password            VARCHAR(256)    NOT NULL,
    email               VARCHAR(70)     NOT NULL,
    email_verified      BOOLEAN         NOT NULL,
    organization_id     INT             NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (organization_id) REFERENCES t_organizations (id)
);

CREATE TABLE t_sessions
(
    id          SERIAL                  NOT NULL,
    expiration  TIMESTAMP               NOT NULL,
    token       VARCHAR(256)            NOT NULL,
    user_id     INT                     NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id)
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
    FOREIGN KEY (user_id) REFERENCES t_users (id),
    FOREIGN KEY (role_id) REFERENCES t_roles (id)
);

CREATE TABLE t_organization_users
(
    id              SERIAL              NOT NULL,
    user_id         INT                 NOT NULL,
    organization_id INT                 NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id),
    FOREIGN KEY (organization_id) REFERENCES t_organizations (id)
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
    FOREIGN KEY (user_id) REFERENCES t_users (id)
);

--############################## Test data ##############################
INSERT INTO t_organizations (id, organization_name, admin_mail)
VALUES
    (1, 'QASQIR', 'erkebulanzholdaskali@gmail.com');

INSERT INTO t_roles (id, role_name)
VALUES
    (1, 'employee'),
    (2, 'company_admin'),
    (3, 'super_admin');

