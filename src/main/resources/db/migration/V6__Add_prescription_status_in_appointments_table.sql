ALTER TABLE user_appointment_schedule 
ADD COLUMN prescription VARCHAR(255) NULL, 
ADD COLUMN appointment_status TEXT NOT NUll default 'pending';