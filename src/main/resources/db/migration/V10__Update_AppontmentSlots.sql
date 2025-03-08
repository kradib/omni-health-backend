ALTER TABLE appointments_slot
ADD CONSTRAINT uq_doctor_slot_date UNIQUE (doctor_id, slot_id, appointment_date);
