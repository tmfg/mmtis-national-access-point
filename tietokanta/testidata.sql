INSERT INTO palveluntuottaja (nimi, ytunnus, kotisivu, kayntiosoite)
VALUES ('Ajopalvelu Testinen Oy', '1234567-8', 'example.com', ROW('Testikatu 1','90666','Testi')::osoite);
