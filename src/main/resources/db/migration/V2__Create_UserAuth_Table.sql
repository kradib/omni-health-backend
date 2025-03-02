CREATE TABLE user_auth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255) NOT NULL DEFAULT 'patient',
    user_detail_id BIGINT UNIQUE,
    FOREIGN KEY (user_detail_id) REFERENCES user_detail(id) ON DELETE CASCADE
);
CREATE INDEX idx_username ON user_auth(username);