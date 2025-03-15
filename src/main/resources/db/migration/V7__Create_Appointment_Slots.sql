CREATE TABLE appointments_slot (
    appointment_slot_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    slot_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    number_of_appointments INT NOT NULL DEFAULT 0,
    FOREIGN KEY (doctor_id) REFERENCES user_detail(id) ON DELETE CASCADE
);
CREATE INDEX idx_doctor_appointment_date ON appointments_slot (doctor_id, appointment_date);
