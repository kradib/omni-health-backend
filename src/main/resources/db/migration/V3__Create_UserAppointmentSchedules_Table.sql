CREATE TABLE user_appointment_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    appointment_date_time TIMESTAMP NOT NULL,
    appointment_place VARCHAR(500) NOT NULL,
    status SMALLINT NOT NULL,
    doctor_name VARCHAR(100) NOT NULL,
    user_details_id BIGINT UNIQUE,
    FOREIGN KEY (user_details_id) REFERENCES user_details(id) ON DELETE CASCADE
);
CREATE INDEX idx_user_details_id ON user_appointment_schedules(user_details_id);