BEGIN;

-- Ryd pair, card og game_pair og nulstil ID'erne.
-- Tabellen game røres ikke, så leaderboardet bevares.
TRUNCATE public.teamb_game_pair, public.teamb_card, public.teamb_pair RESTART IDENTITY;

-- ============================================================
-- LIFE HACK-PAR (pair_id 1-20)
-- ============================================================
INSERT INTO public.teamb_pair (pair_id, is_lifehack, description) VALUES
    (1, TRUE, 'Små ting som sokker, lommetørklæder og løse knapper kan let havne uden for tromlen og ende med at blokere maskinen eller gå i stykker. Læg dem i en vaskepose af netstof, før du starter vasken. Posen holder dem samlet, og den gør vasken mere skånsom for resten af tøjet. Den kan også bruges i tørretumbleren.'),
    (2, TRUE, 'Bagepulver er godt til at binde lugt. Drys et lag over sofaen, også i sprækkerne og under puderne. Lad det ligge flere timer, gerne over natten. Støvsug det derefter grundigt op, så pulveret og lugten følger med.'),
    (3, TRUE, 'Fyld en skål med vand og et par citronskiver. Sæt den i mikroovnen, og varm den op, til vandet damper. Dampen løsner snavset, så du kan tørre det af med en klud. Citronen efterlader også en frisk duft.'),
    (4, TRUE, 'Hæld varmt vand i gryden, så det brændte dækkes. Læg en tablet fra opvaskemaskinen i vandet, og lad det trække, til det brændte løsner sig. Skrub derefter forsigtigt med en svamp, så du ikke behøver at skrubbe hårdt.'),
    (5, TRUE, 'Skrammer fra sko på gulvet kan ofte fjernes med en tennisbold. Gnid bolden hen over skrammen med et let tryk, og tjek, om den er væk. Brug en ren bold, så du ikke får nye mærker.'),
    (6, TRUE, 'Fugerne mellem fliserne samler snavs, der er svært at komme til med en klud. Fugt fugen med vand og et rengøringsmiddel, og skrub den med en gammel tandbørste i små, runde bevægelser. Skyl af med rent vand, når du er færdig.'),
    (7, TRUE, 'Tag låget af en tom sodavandsdåse, og hæng det på en kleshanger. Sæt en ny bøjle i hullet i låget. Du får nu to hængepladser i stedet for én, og det er en nem måde at få mere plads uden at købe noget.'),
    (8, TRUE, 'Natron neutraliserer lugt i stoffer. Drys et tyndt lag over tæppet, og lad det ligge et par timer. Støvsug det derefter op, så både lugt og støv fjernes. Vent med at støvsuge, til tæppet er helt tørt.'),
    (9, TRUE, 'Tandpasta er et mildt skuremiddel, der fjerner matte pletter og skær på vasken. Læg lidt tandpasta på en fugtig klud, og gnid vasken rundt i små cirkler. Skyl grundigt med vand, og tør derefter af med en tør klud.'),
    (10, TRUE, 'Når du ikke har tid til at stryge, kan isterninger ofte glatte stoffet ud. Læg tøjet fladt på et bord, og gnid en isterning over krøllen, til den smelter. Lad tøjet tørre, før du bruger det.'),
    (11, TRUE, 'Når mælken er ved at koge, stiger den hurtigt og løber over kanten. Læg en træske over gryden, så den står på tværs af åbningen. Skeen bryder de bobler, der stiger op, og mælken koger over langt mindre. Hold stadig øje med gryden.'),
    (12, TRUE, 'Kalkpletter er hvide mærker, der dannes, når vandet tørrer på metal. Skær en citron over, og gnid halvdelen rundt på pletten. Syren i citronen opløser kalken. Lad det virke et øjeblik, og skyl derefter med vand og tør af.'),
    (13, TRUE, 'Kaffegrums absorberer lugt, så de er nyttige i fryseren. Fyld et lille skålglas med tørre kaffegrums, og stil det i fryseren. Skift det ud, når det er gammelt, så lugten ikke vender tilbage.'),
    (14, TRUE, 'Hæld en halv kop natron ned i afløbet. Hæld derefter en kop eddike ned, og lad det syde og skumme. Lad det virke i et kvarter, og skyl derefter med varmt vand. Reaktionen løsner snavs og fjerner lugten i rørene.'),
    (15, TRUE, 'Hæld sodavand direkte på pletten, og lad det sive ind i stoffet. Tør derefter pletten af med et håndklæde, så den lettere kommer ud. Vask tøjet som normalt, men tør det ikke endnu. Tjek, at pletten er væk, før du hænger det til tørre.'),
    (16, TRUE, 'Babypudder suger olie op. Drys et tyndt lag over pletten, og lad det ligge nogle minutter, så det kan binde fedtet. Børst pudderet af, og vask derefter tøjet. Hvis pletten stadig er der, så gentag det, før du vasker.'),
    (17, TRUE, 'Læg en tør tepose i skoen over natten. Teen i posen absorberer fugt og lugt, og skoen får en frisk duft. Brug en ny pose, når den er mættet, eller hvis lugten er kraftig.'),
    (18, TRUE, 'Sæbeskum sidder fast på glas og fliser, og det er svært at skrubbe af. Gnid et tørreark hen over området. Arkets overflade løsner skummet, så det lettere kan tørres væk. Tør efter med en ren klud.'),
    (19, TRUE, 'Støv samler sig hurtigt på en ventilator. Tag et pudebetræk over en hånd, og træk bladet ind i det, så støvet ikke falder ned på gulvet. Gentag for hvert blad, så det hele bliver rent.'),
    (20, TRUE, 'Fnug sidder fast i stoffet og gør tøjet slidt. Fugt hænderne let med vand, og stryg dem hen over tøjet i én retning. Fnuget samler sig, og du kan fjerne det med hånden. Tape kan også bruges, hvis du vil have en hurtig løsning.');

-- ============================================================
-- KLASSISKE PAR (pair_id 21-40, ingen beskrivelse)
-- ============================================================
INSERT INTO public.teamb_pair (pair_id, is_lifehack, description) VALUES
    (21, FALSE, NULL), (22, FALSE, NULL), (23, FALSE, NULL), (24, FALSE, NULL),
    (25, FALSE, NULL), (26, FALSE, NULL), (27, FALSE, NULL), (28, FALSE, NULL),
    (29, FALSE, NULL), (30, FALSE, NULL), (31, FALSE, NULL), (32, FALSE, NULL),
    (33, FALSE, NULL), (34, FALSE, NULL), (35, FALSE, NULL), (36, FALSE, NULL),
    (37, FALSE, NULL), (38, FALSE, NULL), (39, FALSE, NULL), (40, FALSE, NULL);

-- ============================================================
-- KORT TIL LIFE HACK-PARRENE (problem og løsning)
-- ============================================================
INSERT INTO public.teamb_card (pair_id, image_url, text_content) VALUES
(1,  '/images/teamb/cards/sokker.png', 'Sokker forsvinder i vasken'),
(1,  '/images/teamb/cards/vaskepose.png', 'Vask i vaskepose'),
(2,  '/images/teamb/cards/sofa.png', 'Sofaen lugter'),
(2,  '/images/teamb/cards/bagepulver.png', 'Drys bagepulver og støvsug'),
(3,  '/images/teamb/cards/mikroovn.png', 'Mikroovnen er snavset'),
(3,  '/images/teamb/cards/citron.png', 'Varm citron og vand op'),
(4,  '/images/teamb/cards/gryde.png', 'Maden er brændt fast'),
(4,  '/images/teamb/cards/tablet.png', 'Opvaskemaskinetablet i blød'),
(5,  '/images/teamb/cards/gulvliste.png', 'Skrammer på gulvlisten'),
(5,  '/images/teamb/cards/tennisbold.png', 'Gnid med en tennisbold'),
(6,  '/images/teamb/cards/fuge.png', 'Snavs i fugerne'),
(6,  '/images/teamb/cards/tandborste.png', 'Skrub med tandbørste'),
(7,  '/images/teamb/cards/garderobe.png', 'For lidt plads i garderoben'),
(7,  '/images/teamb/cards/daaselaag.png', 'Dåselåg på en bøjle'),
(8,  '/images/teamb/cards/taeppe.png', 'Tæppet lugter'),
(8,  '/images/teamb/cards/natron.png', 'Drys natron og støvsug'),
(9,  '/images/teamb/cards/vask.png', 'Vasken er grim'),
(9,  '/images/teamb/cards/tandpasta.png', 'Tandpasta og en fugtig klud'),
(10, '/images/teamb/cards/toej_kroellet.png', 'Tøjet er krøllet'),
(10, '/images/teamb/cards/isterninger.png', 'Gnid med en isterning'),
(11, '/images/teamb/cards/maelk.png', 'Mælken koger over'),
(11, '/images/teamb/cards/traeske.png', 'Træske over gryden'),
(12, '/images/teamb/cards/kalk.png', 'Kalk på vandhanen'),
(12, '/images/teamb/cards/citron_vandhane.png', 'Gnid med en citronhalvdel'),
(13, '/images/teamb/cards/fryser.png', 'Fryseren lugter'),
(13, '/images/teamb/cards/kaffegrums.png', 'Kaffegrums i fryseren'),
(14, '/images/teamb/cards/afloeb.png', 'Afløbet lugter'),
(14, '/images/teamb/cards/natron_eddike.png', 'Natron og eddike i afløbet'),
(15, '/images/teamb/cards/rodvin.png', 'Rødvinsplet på tøjet'),
(15, '/images/teamb/cards/sodavand.png', 'Sodavand på pletten'),
(16, '/images/teamb/cards/olie.png', 'Olieplet på tøjet'),
(16, '/images/teamb/cards/babypudder.png', 'Babypudder over pletten'),
(17, '/images/teamb/cards/sko.png', 'Skoene lugter'),
(17, '/images/teamb/cards/tepose.png', 'Tepose i skoen over natten'),
(18, '/images/teamb/cards/saebeskum.png', 'Sæbeskum på glasset'),
(18, '/images/teamb/cards/toerreark.png', 'Gnid med et tørreark'),
(19, '/images/teamb/cards/ventilator.png', 'Støv på ventilatoren'),
(19, '/images/teamb/cards/pudebetraek.png', 'Pudebetræk over bladet'),
(20, '/images/teamb/cards/fnug.png', 'Tøjet fnugger'),
(20, '/images/teamb/cards/fugtige_haender.png', 'Fugtige hænder over tøjet');

-- ============================================================
-- KORT TIL DE KLASSISKE PAR (to ens kort pr. par)
-- ============================================================
INSERT INTO public.teamb_card (pair_id, image_url, text_content) VALUES
    (21, '/images/teamb/cards/sokker.png', NULL),
    (21, '/images/teamb/cards/sokker.png', NULL),
    (22, '/images/teamb/cards/sofa.png', NULL),
    (22, '/images/teamb/cards/sofa.png', NULL),
    (23, '/images/teamb/cards/mikroovn.png', NULL),
    (23, '/images/teamb/cards/mikroovn.png', NULL),
    (24, '/images/teamb/cards/gryde.png', NULL),
    (24, '/images/teamb/cards/gryde.png', NULL),
    (25, '/images/teamb/cards/gulvliste.png', NULL),
    (25, '/images/teamb/cards/gulvliste.png', NULL),
    (26, '/images/teamb/cards/fuge.png', NULL),
    (26, '/images/teamb/cards/fuge.png', NULL),
    (27, '/images/teamb/cards/garderobe.png', NULL),
    (27, '/images/teamb/cards/garderobe.png', NULL),
    (28, '/images/teamb/cards/taeppe.png', NULL),
    (28, '/images/teamb/cards/taeppe.png', NULL),
    (29, '/images/teamb/cards/vask.png', NULL),
    (29, '/images/teamb/cards/vask.png', NULL),
    (30, '/images/teamb/cards/toej_kroellet.png', NULL),
    (30, '/images/teamb/cards/toej_kroellet.png', NULL),
    (31, '/images/teamb/cards/maelk.png', NULL),
    (31, '/images/teamb/cards/maelk.png', NULL),
    (32, '/images/teamb/cards/kalk.png', NULL),
    (32, '/images/teamb/cards/kalk.png', NULL),
    (33, '/images/teamb/cards/fryser.png', NULL),
    (33, '/images/teamb/cards/fryser.png', NULL),
    (34, '/images/teamb/cards/afloeb.png', NULL),
    (34, '/images/teamb/cards/afloeb.png', NULL),
    (35, '/images/teamb/cards/rodvin.png', NULL),
    (35, '/images/teamb/cards/rodvin.png', NULL),
    (36, '/images/teamb/cards/olie.png', NULL),
    (36, '/images/teamb/cards/olie.png', NULL),
    (37, '/images/teamb/cards/sko.png', NULL),
    (37, '/images/teamb/cards/sko.png', NULL),
    (38, '/images/teamb/cards/saebeskum.png', NULL),
    (38, '/images/teamb/cards/saebeskum.png', NULL),
    (39, '/images/teamb/cards/ventilator.png', NULL),
    (39, '/images/teamb/cards/ventilator.png', NULL),
    (40, '/images/teamb/cards/fnug.png', NULL),
    (40, '/images/teamb/cards/fnug.png', NULL);

-- ============================================================
-- Sæt ID-sekvenserne til den højeste værdi,
-- så næste automatiske ID fortsætter efter 40.
-- ============================================================
SELECT setval(pg_get_serial_sequence('public.teamb_pair', 'pair_id'), (SELECT MAX(pair_id) FROM public.teamb_pair));
SELECT setval(pg_get_serial_sequence('public.teamb_card', 'card_id'), (SELECT MAX(card_id) FROM public.teamb_card));

COMMIT;