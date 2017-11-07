INSERT INTO "transport-operator" (name, "business-id", homepage, "visiting-address", "ckan-group-id")
VALUES ('Ajopalvelu Testinen Oy', '1234567-8', 'http://www.example.com',
ROW('Testikatu 1','90666','Testi')::address, '79046442-ad25-4865-a174-ec199a4b39c4');

INSERT INTO "transport-service"
       ("transport-operator-id","type","terminal","passenger-transportation", "rental","parking",
       "brokerage","created","created-by","modified","modified-by","published?",
       "contact-address", "contact-phone", "homepage")
VALUES
(1,E'passenger-transportation',NULL,E'({},"(www.example.com/url,{})","(,{})",{},"{""(starting,5.9,trip,EUR)"",""(\\\\""basic fare\\\\"",4.9,km,EUR)""}",{child-seat},{},"{wheelchair,walkingstick}","{""(FI,\\\\""esteetöntä on\\\\"")"",""(SV,\\\\""ja, det är\\\\"")"",""(EN,\\\\""very accessible\\\\"")""}",{})',NULL,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,
'(Street 1,90100,Oulu)','123456','www.solita.fi');
