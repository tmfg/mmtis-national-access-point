INSERT INTO "transport-operator" (name, "business-id", homepage, "visiting-address")
VALUES ('Ajopalvelu Testinen Oy', '1234567-8', 'http://www.example.com', ROW('Testikatu 1','90666','Testi')::address);
