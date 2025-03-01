ALTER TABLE user_appointment_schedule 
ADD COLUMN prescription VARCHAR(255) NULL, 
ADD COLUMN appointment_status VARCHAR(255) NOT NUll default 'pending';