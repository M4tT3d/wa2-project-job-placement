/*Add Customers*/
INSERT INTO customer (comment)
VALUES ('test');
INSERT INTO customer (comment)
VALUES ('programming');
/*Add professional*/
INSERT INTO professional (daily_rate, employment_state)
VALUES (100, 'UNEMPLOYED_AVAILABLE');
INSERT INTO professional (daily_rate, employment_state)
VALUES (200, 'UNEMPLOYED_AVAILABLE');
/*Add contacts*/
INSERT INTO contact (name, surname, category, comment, customer_id)
VALUES ('John', 'Doe', 'CUSTOMER', 'This is a comment', 1);
INSERT INTO contact (name, surname, category, comment)
VALUES ('Jane', 'Doe', 'UNKNOWN', 'This is a comment');
INSERT INTO contact (name, surname, category, comment, professional_id)
VALUES ('John', 'Smith', 'PROFESSIONAL', 'This is a comment', 1);
INSERT INTO contact (name, surname, category, comment, customer_id)
VALUES ('Jane', 'Smith', 'CUSTOMER', 'This is a comment', 51);
INSERT INTO contact (name, surname, category, comment)
VALUES ('John', 'Doe', 'UNKNOWN', 'This is a comment');
INSERT INTO contact (name, surname, category, comment)
VALUES ('Jane', 'Doe', 'UNKNOWN', 'This is a comment');
INSERT INTO contact (name, surname, category, comment, professional_id)
VALUES ('Jane', 'Doe', 'PROFESSIONAL', 'This is a comment', 51);
/*Add emails*/
INSERT INTO email (contact_id, email)
VALUES (1, 'john.doe@tt.cc');
INSERT INTO email (contact_id, email)
VALUES (1, 'aa@bb.cc');
/*Add phones*/
INSERT INTO telephone (contact_id, telephone)
VALUES (1, '123456789');
INSERT INTO telephone (contact_id, telephone)
VALUES (1, '987654321');
/*Add addresses*/
INSERT INTO address (contact_id, address)
VALUES (1, 'Some street 1');
INSERT INTO address (contact_id, address)
VALUES (51, 'Some street 2');
