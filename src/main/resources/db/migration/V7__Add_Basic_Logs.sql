CREATE TABLE IF NOT EXISTS t_exception_log
(
    id        SERIAL                NOT NULL,
    cause     VARCHAR(150)          NOT NULL,
    message   VARCHAR(2048)         NOT NULL,
    timestamp TIMESTAMP             NOT NULL DEFAULT current_timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_login_log
(
    id        SERIAL                NOT NULL,
    user_id   BIGINT                NOT NULL,
    timestamp TIMESTAMP             NOT NULL DEFAULT current_timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS t_action_log
(
    id        SERIAL                NOT NULL,
    user_id   BIGINT                NOT NULL,
    action    VARCHAR(250)          NOT NULL,
    endpoint  VARCHAR(250)          NOT NULL,
    data      VARCHAR(2048),
    timestamp TIMESTAMP             NOT NULL DEFAULT current_timestamp,
    PRIMARY KEY (id)
);