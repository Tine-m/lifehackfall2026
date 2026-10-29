
DROP TABLE IF EXISTS public.teama_highscores;

CREATE TABLE IF NOT EXISTS public.teama_highscores
(
    initials character varying(3),
    date date,
    score1 integer,
    score2 integer,
    score3 integer
);