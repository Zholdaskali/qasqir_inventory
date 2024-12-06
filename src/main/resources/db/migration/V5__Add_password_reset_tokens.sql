CREATE TABLE IF NOT EXISTS t_password_reset_tokens (
    id          SERIAL          NOT NULL,
    user_email  VARCHAR(255)    NOT NULL,
    token       VARCHAR(255)    NOT NULL,
    link        VARCHAR(255)    NOT NULL,
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    expires_at  TIMESTAMP       NOT NULL,
    PRIMARY KEY(id)
);
