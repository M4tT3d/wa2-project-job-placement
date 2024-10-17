INSERT INTO job_offer(comment, description, duration, status, value, customer_id, professional_id)
VALUES ('Comment 1', 'JobOffer 1', 1.0, 'CREATED', 0, 1, null);
INSERT INTO job_offer_skills(job_offer_id, skill_id)
VALUES (1, 1), (1, 2);


INSERT INTO job_offer(comment, description, duration, status, value, customer_id, professional_id)
VALUES ('Comment 2', 'JobOffer 2', 2.0, 'CREATED', 0, 1, null);
INSERT INTO job_offer_skills(job_offer_id, skill_id)
VALUES (51, 2), (51, 3);


INSERT INTO job_offer(comment, description, duration, status, value, customer_id, professional_id)
VALUES ('Comment 2', 'JobOffer 2', 2.0, 'SELECTION_PHASE', 0, 1, null);
INSERT INTO job_offer_skills(job_offer_id, skill_id)
VALUES (101, 5), (101, 6);


INSERT INTO job_offer(comment, description, duration, status, value, customer_id, professional_id)
VALUES ('Comment 2', 'JobOffer 2', 2.0, 'CANDIDATE_PROPOSAL', 100.0, 1, 1);
INSERT INTO job_offer_skills(job_offer_id, skill_id)
VALUES (151, 4);


INSERT INTO job_offer(comment, description, duration, status, value, customer_id, professional_id)
VALUES ('Comment 2', 'JobOffer 2', 2.0, 'CONSOLIDATED', 100.0, 1, 1);
INSERT INTO job_offer_skills(job_offer_id, skill_id)
VALUES (201, 7);


INSERT INTO job_offer(comment, description,  duration, status, value, customer_id, professional_id)
VALUES ('Comment 3', 'JobOffer 3', 3.0, 'SELECTION_PHASE', 0, 1, null);
INSERT INTO job_offer_skills(job_offer_id, skill_id)
VALUES (251, 4);


INSERT INTO job_offer(comment, description,  duration, status, value, customer_id, professional_id)
VALUES ('Comment 4', 'JobOffer 4', 4.0, 'CANDIDATE_PROPOSAL', 0, 1, 1);
INSERT INTO job_offer_skills(job_offer_id, skill_id)
VALUES (301, 10);


INSERT INTO job_offer(comment, description,  duration, status, value, customer_id, professional_id)
VALUES ('Comment 4', 'JobOffer 4', 4.0, 'ABORTED', 0, 1, null);
INSERT INTO job_offer_skills(job_offer_id, skill_id)
VALUES (351, 11);


INSERT INTO job_offer(comment, description,  duration, status, value, customer_id, professional_id)
VALUES ('Comment 5', 'JobOffer 5', 5.0, 'CANDIDATE_PROPOSAL', 0, 51, 51);
INSERT INTO job_offer_skills(job_offer_id, skill_id)
VALUES (401, 2), (401, 3);


INSERT INTO job_offer(comment, description,  duration, status, value, customer_id, professional_id)
VALUES ('Comment 6', 'JobOffer 6', 6.0, 'DONE', 0, 51, 51);
INSERT INTO job_offer_skills(job_offer_id, skill_id)
VALUES (451, 4);
