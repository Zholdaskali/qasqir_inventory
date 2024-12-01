DROP TABLE IF EXISTS t_organization CASCADE;

CREATE TABLE IF NOT EXISTS t_images (
    id                  BIGSERIAL NOT NULL,
    organization_name   VARCHAR(255) NOT NULL,
    email               VARCHAR(70)     NOT NULL,
    owner_name          VARCHAR(255)    NOT NULL,
    phone_number        VARCHAR(15)     NOT NULL,
    registration_date   TIMESTAMP       NOT NULL DEFAULT current_timestamp,
    website_link        VARCHAR(255)    NOT NULL,
    image_id            BIGINT          NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (image_id) REFERENCES t_images(id) ON DELETE SET NULL, -- Убираем CASCADE
    UNIQUE (email, phone_number)
);