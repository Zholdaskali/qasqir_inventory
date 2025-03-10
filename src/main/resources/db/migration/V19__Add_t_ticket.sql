CREATE TABLE t_ticket (
    id SERIAL NOT NULL,
    type VARCHAR(10) NOT NULL,
    status VARCHAR(10) NOT NULL,
    document_id BIGINT NOT NULL,
    create_by BIGINT NOT NULL,
    create_at TIMESTAMP NOT NULL,
    manager_id BIGINT NOT NULL,
    managed_at TIMESTAMP NOT NULL,
    comment TEXT,
    FOREIGN KEY (document_id) REFERENCES t_documents(id),
    FOREIGN KEY (create_by) REFERENCES t_users(id),
    FOREIGN KEY (manager_id) REFERENCES t_users(id)
);