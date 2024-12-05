CREATE TABLE UserAuth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_details_id BIGINT UNIQUE,
    FOREIGN KEY (user_details_id) REFERENCES UserDetails(id) ON DELETE CASCADE
);
