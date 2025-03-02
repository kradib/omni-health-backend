CREATE TABLE user_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    major VARCHAR(255) NULL,
    location VARCHAR(255) NULL,
    first_guardian_user_id VARCHAR(15) NULL,
    second_guardian_user_id VARCHAR(15) NULL
);
CREATE INDEX idx_user_detail_id ON user_detail(id);