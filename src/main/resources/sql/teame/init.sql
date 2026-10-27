-- SecurePass (teame ja takkkkk) tabeller til password generatoren
-- Placering i projektet: src/main/resources/sql/teame/init.sql
-- Scriptet kan køres flere gange: det sletter og opretter tabellerne igen så vær opmærksom på det

DROP TABLE IF EXISTS teame_template_character_type;
DROP TABLE IF EXISTS teame_template;
DROP TABLE IF EXISTS teame_character;
DROP TABLE IF EXISTS teame_character_type;

-- Tegntyper, fx store bogstaver og tal
CREATE TABLE teame_character_type
(
    character_type_id SERIAL PRIMARY KEY,
    name              VARCHAR(50) NOT NULL UNIQUE
);

-- Hvert tegn i sin egen række. is_ambiguous markerer tegn, der let forveksles (0/O, l/1/I)
CREATE TABLE teame_character
(
    character_id      SERIAL PRIMARY KEY,
    value             CHAR(1) NOT NULL UNIQUE,
    character_type_id INT     NOT NULL REFERENCES teame_character_type (character_type_id),
    is_ambiguous      BOOLEAN NOT NULL DEFAULT FALSE
);

-- Skabeloner, fx PIN-kode og Wi-Fi
CREATE TABLE teame_template
(
    template_id       SERIAL PRIMARY KEY,
    name              VARCHAR(50) NOT NULL UNIQUE,
    length            INT         NOT NULL CHECK (length BETWEEN 8 AND 128),
    exclude_ambiguous BOOLEAN     NOT NULL DEFAULT FALSE
);

-- Koblingstabel: hvilke tegntyper hver skabelon bruger (M:N)
CREATE TABLE teame_template_character_type
(
    template_id       INT NOT NULL REFERENCES teame_template (template_id) ON DELETE CASCADE,
    character_type_id INT NOT NULL REFERENCES teame_character_type (character_type_id),
    PRIMARY KEY (template_id, character_type_id)
);

-- Data: tegntyper
INSERT INTO teame_character_type (name)
VALUES ('Store bogstaver'),
       ('Små bogstaver'),
       ('Tal'),
       ('Specialtegn');

-- Data: tegn. Hver tekst deles op i ét tegn pr. række
INSERT INTO teame_character (value, character_type_id)
SELECT regexp_split_to_table('ABCDEFGHIJKLMNOPQRSTUVWXYZ', ''),
       (SELECT character_type_id FROM teame_character_type WHERE name = 'Store bogstaver');

INSERT INTO teame_character (value, character_type_id)
SELECT regexp_split_to_table('abcdefghijklmnopqrstuvwxyz', ''),
       (SELECT character_type_id FROM teame_character_type WHERE name = 'Små bogstaver');

INSERT INTO teame_character (value, character_type_id)
SELECT regexp_split_to_table('0123456789', ''),
       (SELECT character_type_id FROM teame_character_type WHERE name = 'Tal');

INSERT INTO teame_character (value, character_type_id)
SELECT regexp_split_to_table('!@#$%^&*()-_=+[]{};:,.?', ''),
       (SELECT character_type_id FROM teame_character_type WHERE name = 'Specialtegn');

-- Markér tegn, der let forveksles
UPDATE teame_character
SET is_ambiguous = TRUE
WHERE value IN ('0', 'O', 'o', 'l', '1', 'I');

-- Data: skabeloner
INSERT INTO teame_template (name, length, exclude_ambiguous)
VALUES ('Maksimal sikkerhed', 32, FALSE),
       ('PIN-kode', 8, FALSE),
       ('Wi-Fi', 16, TRUE);

-- Data: hvilke tegntyper skabelonerne bruger
INSERT INTO teame_template_character_type (template_id, character_type_id)
SELECT t.template_id, ct.character_type_id
FROM teame_template t, teame_character_type ct
WHERE t.name = 'Maksimal sikkerhed';

INSERT INTO teame_template_character_type (template_id, character_type_id)
SELECT t.template_id, ct.character_type_id
FROM teame_template t, teame_character_type ct
WHERE t.name = 'PIN-kode' AND ct.name = 'Tal';

INSERT INTO teame_template_character_type (template_id, character_type_id)
SELECT t.template_id, ct.character_type_id
FROM teame_template t, teame_character_type ct
WHERE t.name = 'Wi-Fi' AND ct.name IN ('Store bogstaver', 'Små bogstaver', 'Tal');
