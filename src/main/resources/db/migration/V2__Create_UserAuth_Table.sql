CREATE TABLE user_auth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_details_id BIGINT UNIQUE,
    FOREIGN KEY (user_details_id) REFERENCES user_details(id) ON DELETE CASCADE
);
CREATE INDEX idx_username ON user_auth(username);