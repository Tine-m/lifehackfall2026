CREATE TABLE IF NOT EXISTS public.teama_highscores
(
    initials character varying(3),
    date date,
    score1 integer,
    score2 integer,
    score3 integer
);
INSERT INTO teama_highscores (initials, score1, score2, score3, date) VALUES
('OWI', 200000, 200000, 200000, '2026-10-26'),
('MAG', 300000, 101000, 200000, '2026-10-25'),
('LED', 300000, 300000, 300000, '2026-10-16'),
('ADH', 400000, 400000, 400000, '2026-10-26');