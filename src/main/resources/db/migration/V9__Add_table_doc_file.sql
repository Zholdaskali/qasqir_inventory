CREATE TABLE t_documents_files (
    id BIGSERIAL PRIMARY KEY,            -- Уникальный идентификатор
    document_id BIGINT NOT NULL,         -- ID документа (связь с t_documents)
    file_name VARCHAR(255) NOT NULL,     -- Название файла
    file_data BYTEA NOT NULL,            -- Поле для хранения PDF (бинарные данные)
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Дата загрузки
    FOREIGN KEY (document_id) REFERENCES t_documents (id) ON DELETE CASCADE
);
