INSERT INTO user_detail (first_name, last_name, email, phone_number, major, location)
VALUES
('John', 'Doe', 'john.doe@example.com', '1234567890', 'Cardiology', 'New York'),
('Alice', 'Smith', 'alice.smith@example.com', '1234567891', 'Neurology', 'Los Angeles'),
('Robert', 'Brown', 'robert.brown@example.com', '1234567892', 'Orthopedics', 'Chicago');


INSERT INTO user_auth (username, password, user_detail_id)
VALUES
('john.doe', 'password_1', (SELECT id FROM user_detail WHERE email = 'john.doe@example.com')),
('alice.smith', 'password_2', (SELECT id FROM user_detail WHERE email = 'alice.smith@example.com')),
('robert.brown', 'password_3', (SELECT id FROM user_detail WHERE email = 'robert.brown@example.com'));

UPDATE user_auth
SET roles = 'doctor'
WHERE username IN ('john.doe', 'alice.smith', 'robert.brown');