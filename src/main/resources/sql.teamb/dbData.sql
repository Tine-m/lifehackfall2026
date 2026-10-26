-- Nulstil data
DELETE FROM public.game_pair;
DELETE FROM public.card;
DELETE FROM public.pair;

-- Par: kolonnelisten og værdierne har samme antal
INSERT INTO public.pair (pair_id, is_lifehack, description) VALUES
    (1,  TRUE, 'Små ting som sokker, lommetørklæder og løse knapper kan let havne uden for tromlen. Læg dem i en vaskepose af netstof, før du starter vasken. Posen holder dem samlet og gør vasken mere skånsom for resten af tøjet.'),
    (2,  TRUE, 'Bagepulver er godt til at binde lugt. Drys et lag over sofaen, også i sprækkerne. Lad det ligge flere timer, og støvsug det derefter grundigt op.'),
    (3,  TRUE, 'Fyld en skål med vand og et par citronskiver. Sæt den i mikroovnen, og varm den op, til vandet damper. Dampen løsner snavset, så du kan tørre det af med en klud.'),
    (4,  TRUE, 'Hæld varmt vand i gryden, så det brændte dækkes. Læg en tablet fra opvaskemaskinen i vandet, og lad det trække, til det brændte løsner sig.'),
    (5,  TRUE, 'Skrammer fra sko på gulvet kan ofte fjernes med en tennisbold. Gnid bolden hen over skrammen med et let tryk, og tjek, om den er væk.'),
    (6,  TRUE, 'Fugerne mellem fliserne samler snavs. Fugt fugen med vand og et rengøringsmiddel, og skrub den med en gammel tandbørste i små, runde bevægelser.'),
    (7,  TRUE, 'Tag låget af en tom sodavandsdåse, og hæng det på en kleshanger. Sæt en ny bøjle i hullet i låget. Du får to hængepladser i stedet for én.'),
    (8,  TRUE, 'Natron neutraliserer lugt i stoffer. Drys et tyndt lag over tæppet, og lad det ligge et par timer. Støvsug det derefter op, når tæppet er helt tørt.'),
    (9,  TRUE, 'Tandpasta er et mildt skuremiddel, der fjerner matte pletter og skær på vasken. Læg lidt på en fugtig klud, gnid i små cirkler, og skyl grundigt med vand.'),
    (10, TRUE, 'Når du ikke har tid til at stryge, kan isterninger ofte glatte stoffet ud. Læg tøjet fladt, og gnid en isterning over krøllen, til den smelter.'),
    (11, TRUE, 'Når mælken er ved at koge, stiger den hurtigt og løber over kanten. Læg en træske over gryden, så den står på tværs af åbningen. Skeen bryder boblerne, så mælken koger over langt mindre.'),
    (12, TRUE, 'Kalkpletter er hvide mærker, der dannes, når vandet tørrer på metal. Skær en citron over, og gnid halvdelen rundt på pletten. Syren opløser kalken, og du skyller derefter med vand.'),
    (13, TRUE, 'Kaffegrums absorberer lugt, så de er nyttige i fryseren. Fyld et lille skålglas med tørre kaffegrums, og stil det i fryseren. Skift det ud, når det er gammelt.'),
    (14, TRUE, 'Hæld en halv kop natron ned i afløbet. Hæld derefter en kop eddike ned, og lad det syde og skumme. Lad det virke i et kvarter, og skyl derefter med varmt vand.'),
    (15, TRUE, 'Hæld sodavand direkte på pletten, og lad det sive ind i stoffet. Tør derefter pletten af med et håndklæde. Vask tøjet som normalt, men tør det ikke endnu.'),
    (16, TRUE, 'Babypudder suger olie op. Drys et tyndt lag over pletten, og lad det ligge nogle minutter. Børst pudderet af, og vask derefter tøjet.'),
    (17, TRUE, 'Læg en tør tepose i skoen over natten. Teen i posen absorberer fugt og lugt, og skoen får en frisk duft. Brug en ny pose, når den er mættet.'),
    (18, TRUE, 'Sæbeskum sidder fast på glas og fliser. Gnid et tørreark hen over området, så skummet løsner sig. Tør efter med en ren klud.'),
    (19, TRUE, 'Støv samler sig hurtigt på en ventilator. Tag et pudebetræk over en hånd, og træk bladet ind i det, så støvet ikke falder ned. Gentag for hvert blad.'),
    (20, TRUE, 'Fnug sidder fast i stoffet og gør tøjet slidt. Fugt hænderne let med vand, og stryg dem hen over tøjet i én retning. Fnuget samler sig, og du kan fjerne det med hånden.');


SELECT setval(pg_get_serial_sequence('public.pair', 'pair_id'), 20);

-- Kort: to pr. par (problem og løsning)
INSERT INTO public.card (pair_id, image_url, text_content) VALUES
    (1,  '/images/teamb/cards/sokker.png', NULL),
    (1,  '/images/teamb/cards/vaskepose.png', NULL),
    (2,  '/images/teamb/cards/sofa.png', NULL),
    (2,  '/images/teamb/cards/bagepulver.png', NULL),
    (3,  '/images/teamb/cards/mikroovn.png', NULL),
    (3,  '/images/teamb/cards/citron.png', NULL),
    (4,  '/images/teamb/cards/gryde.png', NULL),
    (4,  '/images/teamb/cards/tablet.png', NULL),
    (5,  '/images/teamb/cards/gulvliste.png', NULL),
    (5,  '/images/teamb/cards/tennisbold.png', NULL),
    (6,  '/images/teamb/cards/fuge.png', NULL),
    (6,  '/images/teamb/cards/tandborste.png', NULL),
    (7,  '/images/teamb/cards/garderobe.png', NULL),
    (7,  '/images/teamb/cards/daaselaag.png', NULL),
    (8,  '/images/teamb/cards/taeppe.png', NULL),
    (8,  '/images/teamb/cards/natron.png', NULL),
    (9,  '/images/teamb/cards/vask.png', NULL),
    (9,  '/images/teamb/cards/tandpasta.png', NULL),
    (10, '/images/teamb/cards/toej_kroellet.png', NULL),
    (10, '/images/teamb/cards/isterninger.png', NULL),
    (11, '/images/teamb/cards/maelk.png', NULL),
    (11, '/images/teamb/cards/traeske.png', NULL),
    (12, '/images/teamb/cards/kalk.png', NULL),
    (12, '/images/teamb/cards/citron_vandhane.png', NULL),
    (13, '/images/teamb/cards/fryser.png', NULL),
    (13, '/images/teamb/cards/kaffegrums.png', NULL),
    (14, '/images/teamb/cards/afloeb.png', NULL),
    (14, '/images/teamb/cards/natron_eddike.png', NULL),
    (15, '/images/teamb/cards/rodvin.png', NULL),
    (15, '/images/teamb/cards/sodavand.png', NULL),
    (16, '/images/teamb/cards/olie.png', NULL),
    (16, '/images/teamb/cards/babypudder.png', NULL),
    (17, '/images/teamb/cards/sko.png', NULL),
    (17, '/images/teamb/cards/tepose.png', NULL),
    (18, '/images/teamb/cards/saebeskum.png', NULL),
    (18, '/images/teamb/cards/toerreark.png', NULL),
    (19, '/images/teamb/cards/ventilator.png', NULL),
    (19, '/images/teamb/cards/pudebetraek.png', NULL),
    (20, '/images/teamb/cards/fnug.png', NULL),
    (20, '/images/teamb/cards/fugtige_haender.png', NULL);

SELECT setval(pg_get_serial_sequence('public.card', 'card_id'), (SELECT MAX(card_id) FROM public.card));