CREATE TABLE t_nomenclature_size_history (
    id BIGSERIAL PRIMARY KEY,
    nomenclature_id BIGINT NOT NULL,
    width DECIMAL(20,10),
    height DECIMAL(20,10),
    length DECIMAL(20,10),
    volume DECIMAL(20,10),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by BIGINT,
    FOREIGN KEY (nomenclature_id) REFERENCES t_nomenclature(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES t_users(id) ON DELETE SET NULL
);