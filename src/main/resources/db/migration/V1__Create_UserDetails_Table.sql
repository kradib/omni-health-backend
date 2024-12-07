CREATE TABLE user_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    first_guardian_user_id VARCHAR(15) NULL,
    second_guardian_user_id VARCHAR(15) NULL
);
