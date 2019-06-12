INSERT INTO "transport-operator" (name, "business-id", homepage, "visiting-address", "ckan-group-id")
VALUES ('Ajopalvelu Testinen Oy', '1234567-8', 'http://www.example.com',
ROW('Testikatu 1','90666','Testi')::address, '79046442-ad25-4865-a174-ec199a4b39c4');

INSERT INTO "public"."transport-service" ("transport-operator-id", type, terminal, "passenger-transportation", rentals, parking, brokerage, created, "created-by", modified, "modified-by", published, "contact-address", "contact-phone", "contact-gsm", "contact-email", homepage, name, "sub-type", companies, "brokerage?", description, "available-from", "available-to", "notice-external-interfaces?", "companies-csv-url", "company-source", "company-csv-filename", "transport-type")
VALUES
  (1, E'passenger-transportation', NULL,
      E'({},"(,{})","(,{})",{},"{""(starting,5.9,trip,)"",""(\\\\""basic fare\\\\"",4.9,km,)""}",{},"{""(\\\\""{MON,TUE,WED,THU,FRI,SAT,SUN}\\\\"",00:00:00,24:00:00,{},t)""}",{},{},{},"(,{})",{},{},,{},{},{},{},{},{},{},"{""(FI,*FI*)"",""(SV,*SV*)"",""(EN,*EN*)""}",mandatory)',
      NULL, NULL, NULL, E'2017-12-04 14:51:06.539000 +02:00', E'401139db-8f3e-4371-8233-5d51d4c4c8b6',
      E'2018-06-11 16:12:50.550000', E'401139db-8f3e-4371-8233-5d51d4c4c8b6', to_timestamp(0), E'("Street 1",90100,Oulu)',
      E'123456', NULL, NULL, E'www.solita.fi',E'Taksi', E'taxi', NULL, FALSE,NULL, NULL, NULL, TRUE, NULL,
      NULL, NULL, E'{road}');

INSERT INTO "public"."transport-service" ("transport-operator-id", type, terminal, "passenger-transportation", rentals, parking, brokerage, created, "created-by", modified, "modified-by", published, "contact-address", "contact-phone", "contact-gsm", "contact-email", homepage, name, "sub-type", companies, "brokerage?", description, "available-from", "available-to", "notice-external-interfaces?", "companies-csv-url", "company-source", "company-csv-filename", "transport-type")
VALUES
  (1, E'passenger-transportation', NULL,
      E'({},"(,{})","(,{})",{},"{""(starting,5.9,trip,)"",""(\\\\""basic fare\\\\"",4.9,km,)""}",{},"{""(\\\\""{MON,TUE,WED,THU,FRI,SAT,SUN}\\\\"",00:00:00,24:00:00,{},t)""}",{},{},{},"(,{})",{},{},,{},{},{},{},{},{},{},"{""(FI,*FI*)"",""(SV,*SV*)"",""(EN,*EN*)""}",mandatory)',
      NULL, NULL, NULL, E'2017-12-04 14:51:06.539000 +02:00', E'401139db-8f3e-4371-8233-5d51d4c4c8b6',
      E'2018-06-11 16:12:50.550000', E'401139db-8f3e-4371-8233-5d51d4c4c8b6', to_timestamp(0), E'("Street 1",90100,Oulu)',
      E'123456', NULL, NULL, E'www.solita.fi',E'Säännöllinen aikataulun mukainen liikenne', E'schedule', NULL, FALSE,NULL, NULL, NULL, TRUE, NULL,
      NULL, NULL, E'{road}');

INSERT INTO "transport-operator" (name, "business-id", homepage, "visiting-address", "ckan-group-id")
VALUES ('Terminaali Oy', '1234567-8', 'http://www.example.com',
ROW('Terminaalitie 1','90100','Terminaalikaupunki')::address, 'ff5ca54d-2ff5-476d-9ad4-e903b6d1eeb4');

INSERT INTO "public"."finnish_ports"("code","name","location","created")
VALUES
(E'OTE141',E'{"(FI,\\"Puerto Mont\\")"}',E'POINT(-72.917481 -41.474117)',E'2018-04-27 10:01:51.364+00'),
(E'OTE140',E'{"(FI,Panama)"}',E'POINT(-79.498291 8.995635)',E'2018-04-27 10:01:51.342+00'),
(E'OTE139',E'{"(FI,\\"Los Angeles\\")"}',E'POINT(-118.349611 33.474982)',E'2018-04-27 10:01:51.304+00'),
(E'OTE138',E'{"(FI,\\"Middleton Island\\")"}',E'POINT(-146.318054 59.430127)',E'2018-04-27 07:06:00.846+00');
