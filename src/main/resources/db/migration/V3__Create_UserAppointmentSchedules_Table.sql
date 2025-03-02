CREATE TABLE user_appointment_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    appointment_date_time TIMESTAMP NOT NULL,
    prescription VARCHAR(255) NULL,
    appointment_status VARCHAR(255) NOT NUll default 'pending',
    user_detail_id BIGINT NOT NULL,
    doctor_detail_id BIGINT NOT NULL,
    FOREIGN KEY (user_detail_id) REFERENCES user_detail(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_detail_id) REFERENCES user_detail(id) ON DELETE CASCADE
);
CREATE INDEX idx_user_detail_id ON user_appointment_schedule(user_detail_id);