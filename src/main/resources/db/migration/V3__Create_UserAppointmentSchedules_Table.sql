CREATE TABLE user_appointment_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    appointment_date_time TIMESTAMP NOT NULL,
    appointment_place VARCHAR(500) NOT NULL,
    status SMALLINT NOT NULL,
    doctor_name VARCHAR(100) NOT NULL,
    user_detail_id BIGINT NOT NULL,
    FOREIGN KEY (user_detail_id) REFERENCES user_detail(id) ON DELETE CASCADE
);
CREATE INDEX idx_user_detail_id ON user_appointment_schedule(user_detail_id);