CREATE TABLE document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL
);

CREATE INDEX idx_document_user_name ON document(user_name);