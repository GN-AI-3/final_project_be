-- Schedule dummy data
-- Day of week: 0 = Sunday, 1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday

-- user1@test.com schedules (Monday, Wednesday, Friday)
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 1, true, 'user1@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 3, true, 'user1@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 5, true, 'user1@test.com');

-- user2@test.com schedules (Tuesday, Thursday)
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 2, true, 'user2@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 4, true, 'user2@test.com');

-- user3@test.com schedules (Monday to Friday)
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 1, true, 'user3@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 2, true, 'user3@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 3, true, 'user3@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 4, true, 'user3@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 5, true, 'user3@test.com');

-- user4@test.com schedules (Monday, Tuesday, Thursday, Friday)
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 1, true, 'user4@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 2, true, 'user4@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 4, true, 'user4@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 5, true, 'user4@test.com');

-- user5@test.com schedules (Tuesday, Thursday, Saturday)
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 2, true, 'user5@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 4, true, 'user5@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 6, true, 'user5@test.com');

-- user6@test.com schedules (Wednesday, Friday, Sunday)
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 0, true, 'user6@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 3, true, 'user6@test.com');
INSERT INTO schedule (created_at, modified_at, day_of_week, active, member_id) 
VALUES (NOW(), NOW(), 5, true, 'user6@test.com');

-- Attendance dummy data for the past month
-- Status: 'PRESENT', 'ABSENT', 'LATE'

-- user1@test.com attendance records
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-03', 'PRESENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-05', 'PRESENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-07', 'ABSENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-10', 'PRESENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-12', 'LATE', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-14', 'PRESENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-17', 'PRESENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-19', 'PRESENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-21', 'ABSENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-24', 'PRESENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-26', 'PRESENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-28', 'PRESENT', 'user1@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-31', 'LATE', 'user1@test.com');

-- user2@test.com attendance records
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-04', 'PRESENT', 'user2@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-06', 'ABSENT', 'user2@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-11', 'PRESENT', 'user2@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-13', 'LATE', 'user2@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-18', 'PRESENT', 'user2@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-20', 'PRESENT', 'user2@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-25', 'ABSENT', 'user2@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-27', 'PRESENT', 'user2@test.com');

-- user3@test.com attendance records
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-03', 'ABSENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-04', 'ABSENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-05', 'LATE', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-06', 'PRESENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-07', 'PRESENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-10', 'ABSENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-11', 'ABSENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-12', 'PRESENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-13', 'PRESENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-14', 'ABSENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-17', 'ABSENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-18', 'LATE', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-19', 'PRESENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-20', 'ABSENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-21', 'PRESENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-24', 'LATE', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-25', 'PRESENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-26', 'ABSENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-27', 'PRESENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-28', 'PRESENT', 'user3@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-31', 'ABSENT', 'user3@test.com');

-- user4@test.com attendance records
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-03', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-04', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-07', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-10', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-11', 'LATE', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-14', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-17', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-18', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-21', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-24', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-25', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-28', 'PRESENT', 'user4@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-31', 'ABSENT', 'user4@test.com');

-- user5@test.com attendance records
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-04', 'PRESENT', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-06', 'LATE', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-08', 'PRESENT', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-11', 'PRESENT', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-13', 'ABSENT', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-15', 'PRESENT', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-18', 'PRESENT', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-20', 'ABSENT', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-22', 'PRESENT', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-25', 'LATE', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-27', 'PRESENT', 'user5@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-29', 'PRESENT', 'user5@test.com');

-- user6@test.com attendance records
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-02', 'PRESENT', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-05', 'PRESENT', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-07', 'ABSENT', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-09', 'PRESENT', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-12', 'LATE', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-14', 'ABSENT', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-16', 'PRESENT', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-19', 'PRESENT', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-21', 'ABSENT', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-23', 'PRESENT', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-26', 'PRESENT', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-28', 'LATE', 'user6@test.com');
INSERT INTO attendance (created_at, modified_at, attendance_date, status, member_id) 
VALUES (NOW(), NOW(), '2025-03-30', 'PRESENT', 'user6@test.com'); 