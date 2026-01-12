INSERT INTO "transport-operator" (name, "business-id", homepage, "visiting-address", "ckan-group-id")
VALUES ('Ajopalvelu Testinen Oy', '1234567-8', 'http://www.example.com',
ROW('Testikatu 1','Testi','90666','FI')::address, '79046442-ad25-4865-a174-ec199a4b39c4');

INSERT INTO "public"."transport-service" ("transport-operator-id", type, terminal, "passenger-transportation", rentals, parking, brokerage, created, "created-by", modified, "modified-by", published, "contact-address", "contact-phone", "contact-gsm", "contact-email", homepage, name, "sub-type", companies, "brokerage?", description, "available-from", "available-to", "notice-external-interfaces?", "companies-csv-url", "company-source", "company-csv-filename", "transport-type")
VALUES
  (1, E'passenger-transportation', NULL,
      E'({},"(,{})","(,{})",{},"{""(starting,5.9,trip,)"",""(\\\\""basic fare\\\\"",4.9,km,)""}",{},"{""(\\\\""{MON,TUE,WED,THU,FRI,SAT,SUN}\\\\"",00:00:00,24:00:00,{},t)""}",{},{},{},"(,{})",{},{},,{},{},{},{},{},{},{},"{""(FI,*FI*)"",""(SV,*SV*)"",""(EN,*EN*)""}",mandatory)',
      NULL, NULL, NULL, E'2017-12-04 14:51:06.539000 +02:00', E'401139db-8f3e-4371-8233-5d51d4c4c8b6',
      E'2018-06-11 16:12:50.550000', E'401139db-8f3e-4371-8233-5d51d4c4c8b6', to_timestamp(0), E'("Street 1",Oulu,90100,FI)',
      E'123456', NULL, NULL, E'www.solita.fi',E'Taksi', E'taxi', NULL, FALSE,NULL, NULL, NULL, TRUE, NULL,
      NULL, NULL, E'{road}');


INSERT INTO public.operation_area ("transport-service-id", description, location, "primary?", feature_id)
values (1, '{"(FI,\"90940 Jääli\")"}', 'MULTIPOLYGON (((25.69530533944974 65.0562387672807, 25.65778996432365 65.05766473355477, 25.65139156778613 65.06033970406806, 25.65134749882993 65.06035449595481, 25.645508082100847 65.06171453330593, 25.640419151575827 65.06127150487728, 25.635270675252954 65.05938257744242, 25.629800831214585 65.05885467123339, 25.623807621553166 65.05888548995594, 25.623006867588717 65.0587924485616, 25.622330946424597 65.0588002235958, 25.621710519949886 65.05914209217498, 25.621723758349177 65.05915760788818, 25.62171488179061 65.05932938488742, 25.62275547181354 65.05979771150085, 25.619973178958624 65.06155984146011, 25.61894892583405 65.06316464449696, 25.61611927888481 65.06491174436934, 25.616119282434358 65.06491188081309, 25.616121920706934 65.06491467201585, 25.597027184559565 65.08246966338811, 25.59698318472738 65.08250965670678, 25.59694871723791 65.08254098567146, 25.596940706469724 65.08254826695158, 25.608601047044957 65.10387063941427, 25.688311954589736 65.12509352200895, 25.71181413684608 65.12176947968325, 25.722984703678343 65.11994182228321, 25.72948644878074 65.1180947409952, 25.729658646055267 65.11805677902335, 25.729918702927925 65.1178202874627, 25.729903396505254 65.11719630071094, 25.728623615527866 65.11493234759149, 25.728581634126215 65.11402309023211, 25.730712210038202 65.11263751657137, 25.758051034928304 65.11081173175516, 25.746524264972717 65.06103457954504, 25.69530533944974 65.0562387672807)))', true, null);


INSERT INTO "public"."transport-service" ("transport-operator-id", type, terminal, "passenger-transportation", rentals, parking, brokerage, created, "created-by", modified, "modified-by", published, "contact-address", "contact-phone", "contact-gsm", "contact-email", homepage, name, "sub-type", companies, "brokerage?", description, "available-from", "available-to", "notice-external-interfaces?", "companies-csv-url", "company-source", "company-csv-filename", "transport-type")
VALUES
  (1, E'passenger-transportation', NULL,
      E'({},"(,{})","(,{})",{},"{""(starting,5.9,trip,)"",""(\\\\""basic fare\\\\"",4.9,km,)""}",{},"{""(\\\\""{MON,TUE,WED,THU,FRI,SAT,SUN}\\\\"",00:00:00,24:00:00,{},t)""}",{},{},{},"(,{})",{},{},,{},{},{},{},{},{},{},"{""(FI,*FI*)"",""(SV,*SV*)"",""(EN,*EN*)""}",mandatory)',
      NULL, NULL, NULL, E'2017-12-04 14:51:06.539000 +02:00', E'401139db-8f3e-4371-8233-5d51d4c4c8b6',
      E'2018-06-11 16:12:50.550000', E'401139db-8f3e-4371-8233-5d51d4c4c8b6', to_timestamp(0), E'("Street 1",Oulu,90100,FI)',
      E'123456', NULL, NULL, E'www.solita.fi',E'Säännöllinen aikataulun mukainen liikenne', E'schedule', NULL, FALSE,NULL, NULL, NULL, TRUE, NULL,
      NULL, NULL, E'{road}');

INSERT INTO "transport-operator" (name, "business-id", homepage, "visiting-address", "ckan-group-id")
VALUES ('Terminaali Oy', '1234567-9', 'http://www.example.com',
ROW('Terminaalitie 1','Terminaalikaupunki','90100','FI')::address, 'ff5ca54d-2ff5-476d-9ad4-e903b6d1eeb4');

INSERT INTO "public"."finnish_ports"("code","name","location","created")
VALUES
(E'OTE141',E'{"(FI,\\"Puerto Mont\\")"}',E'POINT(-72.917481 -41.474117)',E'2018-04-27 10:01:51.364+00'),
(E'OTE140',E'{"(FI,Panama)"}',E'POINT(-79.498291 8.995635)',E'2018-04-27 10:01:51.342+00'),
(E'OTE139',E'{"(FI,\\"Los Angeles\\")"}',E'POINT(-118.349611 33.474982)',E'2018-04-27 10:01:51.304+00'),
(E'OTE138',E'{"(FI,\\"Middleton Island\\")"}',E'POINT(-146.318054 59.430127)',E'2018-04-27 07:06:00.846+00');
