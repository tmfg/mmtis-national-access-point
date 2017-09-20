-- Liikkumispalveluiden olennaiset tiedot


CREATE TYPE osoite AS (
  katuosoite VARCHAR(128),
  postinumero CHAR(5),
  postitoimipaikka VARCHAR(64)
);

CREATE TABLE palveluntuottaja (
  id SERIAL PRIMARY KEY,
  nimi VARCHAR(200) NOT NULL, -- PRH ei kerro maksimipituutta
  ytunnus CHAR(9) NOT NULL,
  kotisivu VARCHAR(200),
  kayntiosoite osoite,
  postiosoite osoite
);


CREATE TYPE liikkumispalvelutyyppi AS ENUM (
  'satama','kuljetus','vuokraus','pysakointi','valityspalvelu');

CREATE TABLE liikkumispalvelu (
  id SERIAL PRIMARY KEY,
  "palveluntuottaja-id" INTEGER REFERENCES palveluntuottaja (id) NOT NULL,
  tyyppi liikkumispalvelutyyppi NOT NULL
);



-- Aikatauluja varten
CREATE TYPE viikonpaiva AS ENUM ('MA','TI','KE','TO','PE','LA','SU');
