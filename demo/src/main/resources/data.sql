
INSERT IGNORE INTO customer_details (
    username, password, aadhar_number, permanent_address, state, country, city,
    email, phone_number, status, dob, age, gender, father_name, mother_name
) VALUES
('user1','pass1','123456789012','123 Street A','Maharashtra','India','Mumbai','user1@email.com','9876543210','active','1995-01-15',30,'Male','Father1','Mother1'),
('user2','pass2','223456789012','234 Street B','Delhi','India','New Delhi','user2@email.com','9876543211','active','1996-02-20',29,'Female','Father2','Mother2'),
('user3','pass3','323456789012','345 Street C','Karnataka','India','Bangalore','user3@email.com','9876543212','inactive','1990-03-10',35,'Male','Father3','Mother3'),
('user4','pass4','423456789012','456 Street D','Tamil Nadu','India','Chennai','user4@email.com','9876543213','active','1992-04-25',33,'Female','Father4','Mother4'),
('user5','pass5','523456789012','567 Street E','West Bengal','India','Kolkata','user5@email.com','9876543214','inactive','1998-05-30',27,'Male','Father5','Mother5'),
('user6','pass6','623456789012','678 Street F','Gujarat','India','Ahmedabad','user6@email.com','9876543215','active','1994-06-18',31,'Female','Mother6','Father6'),
('user7','pass7','723456789012','789 Street G','Punjab','India','Amritsar','user7@email.com','9876543216','active','1993-07-22',32,'Male','Father7','Mother7'),
('user8','pass8','823456789012','890 Street H','Kerala','India','Kochi','user8@email.com','9876543217','inactive','1991-08-05',34,'Female','Father8','Mother8'),
('user9','pass9','923456789012','901 Street I','Rajasthan','India','Jaipur','user9@email.com','9876543218','active','1997-09-09',28,'Male','Father9','Mother9'),
('user10','pass10','103456789012','012 Street J','Bihar','India','Patna','user10@email.com','9876543219','active','1999-10-12',26,'Female','Father10','Mother10');


