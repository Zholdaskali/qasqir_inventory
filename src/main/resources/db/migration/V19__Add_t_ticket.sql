CREATE TABLE t_ticket (
    id SERIAL NOT NULL,
    type VARCHAR(10) NOT NULL,
    status VARCHAR(10) NOT NULL,
    document_id BIGINT NOT NULL,
    create_by BIGINT NOT NULL,
    create_at TIMESTAMP NOT NULL,
    manager_id BIGINT,
    managed_at TIMESTAMP,
    comment TEXT,
    inventory_id BIGINT NOT NULL,
    quantity NUMERIC(15, 3) DEFAULT 0,
    FOREIGN KEY (document_id) REFERENCES t_documents(id),
    FOREIGN KEY (create_by) REFERENCES t_users(id),
    FOREIGN KEY (manager_id) REFERENCES t_users(id),
    FOREIGN KEY (inventory_id) REFERENCES t_inventory(id)
);