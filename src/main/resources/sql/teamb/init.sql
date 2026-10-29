-- Kør denne fil først, derefter dbData.sql

BEGIN;

DROP TABLE IF EXISTS public.teamb_game_pair CASCADE;
DROP TABLE IF EXISTS public.teamb_card CASCADE;
DROP TABLE IF EXISTS public.teamb_game CASCADE;
DROP TABLE IF EXISTS public.teamb_pair CASCADE;

CREATE TABLE public.teamb_pair
(
    pair_id     serial PRIMARY KEY,
    is_lifehack boolean NOT NULL DEFAULT false,
    description text
);

CREATE TABLE public.teamb_card
(
    card_id      serial PRIMARY KEY,
    pair_id      integer NOT NULL REFERENCES public.teamb_pair (pair_id) ON DELETE CASCADE,
    image_url    character varying(500),
    text_content character varying(255)
);

CREATE TABLE public.teamb_game
(
    game_id          serial PRIMARY KEY,
    player_name      character varying(30),
    is_lifehack      boolean NOT NULL DEFAULT false,
    score            integer NOT NULL,
    duration_seconds integer NOT NULL,
    finished_at      timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.teamb_game_pair
(
    game_id integer NOT NULL REFERENCES public.teamb_game (game_id) ON DELETE CASCADE,
    pair_id integer NOT NULL REFERENCES public.teamb_pair (pair_id),
    PRIMARY KEY (game_id, pair_id)
);

COMMIT;