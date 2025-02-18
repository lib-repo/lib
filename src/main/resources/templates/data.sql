ALTER TABLE subject MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;


-- 도서 주제 삽입
INSERT INTO Subject (subject_id, name) VALUES (1, '총류');
INSERT INTO Subject (subject_id, name) VALUES (2, '철학');
INSERT INTO Subject (subject_id, name) VALUES (3, '종교');
INSERT INTO Subject (subject_id, name) VALUES (4, '사회과학');
INSERT INTO Subject (subject_id, name) VALUES (5, '자연과학');
INSERT INTO Subject (subject_id, name) VALUES (6, '기술과학');
INSERT INTO Subject (subject_id, name) VALUES (7, '예술');
INSERT INTO Subject (subject_id, name) VALUES (8, '언어');
INSERT INTO Subject (subject_id, name) VALUES (9, '문학');
INSERT INTO Subject (subject_id, name) VALUES (10, '역사');
