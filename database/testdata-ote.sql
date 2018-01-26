INSERT INTO "transport-operator" (name, "business-id", homepage, "visiting-address", "ckan-group-id")
VALUES ('Ajopalvelu Testinen Oy', '1234567-8', 'http://www.example.com',
ROW('Testikatu 1','90666','Testi')::address, '79046442-ad25-4865-a174-ec199a4b39c4');

INSERT INTO "public"."transport-service"("id","transport-operator-id","type","terminal","passenger-transportation","rentals","parking","brokerage","created","created-by","modified","modified-by","published?","contact-address","contact-phone","contact-gsm","contact-email","homepage","name","ckan-dataset-id","ckan-resource-id","sub-type","companies","brokerage?")
VALUES
(1,1,E'passenger-transportation',NULL,E'({},"(,{})","(,{})",{},"{""(starting,5.9,trip,)"",""(\\\\""basic fare\\\\"",4.9,km,)""}",{},{},{},{},{},"(,{})",{},{},,{},{},{},{},{},{},{},"{""(FI,*FI*)"",""(SV,*SV*)"",""(EN,*EN*)""}")',NULL,NULL,NULL,E'2017-12-04 12:51:06.539+00',E'401139db-8f3e-4371-8233-5d51d4c4c8b6',E'2017-12-04 12:57:34.93+00',E'401139db-8f3e-4371-8233-5d51d4c4c8b6',FALSE,E'("Street 1",90100,Oulu)',E'123456',NULL,NULL,E'www.solita.fi',E'Taksi',NULL,NULL,E'taxi',NULL,FALSE);


INSERT INTO "transport-operator" (name, "business-id", homepage, "visiting-address", "ckan-group-id")
VALUES ('Terminaali Oy', '1234567-8', 'http://www.example.com',
ROW('Terminaalitie 1','90100','Terminaalikaupunki')::address, 'ff5ca54d-2ff5-476d-9ad4-e903b6d1eeb4');