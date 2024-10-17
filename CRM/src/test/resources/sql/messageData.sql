INSERT INTO message (comment, body, channel, date, priority, sender, state, subject)
VALUES ('comment', 'body', 'EMAIL', '2021-01-01 00:00:00', 'HIGH', 'alessia@live.com', 'READ', 'subject');
INSERT INTO message (comment, body, channel, date, priority, sender, state, subject)
VALUES ('comment', 'body', 'PHONE_CALL', '2024-01-01 00:00:00', 'LOW', '1234567890', 'RECEIVED', 'subject');

/*ADD HISTORY*/
INSERT INTO history (comment, date, messages_id, state)
VALUES ('comment', '2021-01-01 00:00:00', 1, 'RECEIVED');
INSERT INTO history (comment, date, messages_id, state)
VALUES ('comment', '2024-01-01 00:00:00', 1, 'READ');
INSERT INTO history (comment, date, messages_id, state)
VALUES ('comment', '2021-01-01 00:00:00', 51, 'RECEIVED');