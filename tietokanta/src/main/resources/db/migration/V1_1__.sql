-- Liikkumispalveluiden olennaiset tiedot


CREATE TYPE osoite AS (
  katuosoite VARCHAR(128),
  postinumero CHAR(5),
  postitoimipaikka VARCHAR(64)
);

CREATE TABLE palveluntuottaja (
  id SERIAL PRIMARY KEY,
  nimi VARCHAR(200) NOT NULL, -- PRH ei kerro maksimipituutta
  ytunnus CHAR(9),
  kotisivu VARCHAR(1024),
  kayntiosoite osoite,
  postiosoite osoite,
  puhelin VARCHAR(16),
  gsm VARCHAR(16)
);

CREATE TYPE viikonpaiva AS ENUM ('MA','TI','KE','TO','PE','LA','SU');

CREATE TYPE aukioloaika AS (
  viikonpaivat viikonpaiva[],
  avaamisaika TIME,
  sulkemisaika TIME
);

CREATE TYPE liikkumispalvelutyyppi AS ENUM (
  'terminaali', -- Satamat, asemat ja terminaalit
  'henkilokuljetus', -- henkilöiden kuljetuspalvelut
  'vuokraus', -- Vuokraus- ja yhteiskäyttöpalvelut
  'pysakointi', -- Pysäköintipalvelut
  'valitys' -- Välityspalvelut
);

CREATE TYPE esteettomyystuki AS ENUM (
  'inva-wc', 'yleinen-esteettomyys', 'avustuspalvelut', 'induktiosilmukka'
  -- FIXME: missä wirallinen lista?
);

CREATE TYPE erityispalvelu AS ENUM (
 'lastenhoitotila', 'tupakointitila'
 -- FIXME: selevitä wirallinen lista
);

CREATE TYPE liikennevalinetyyppi AS ENUM (
  'henkiloauto','moottoripyora','mopo','vene'
  -- FIXME: onko muita, tarvitaanko laivoista ja lentovehkeistä?
);

CREATE TYPE maksutapa AS ENUM (
  'kateinen', 'pankkikortti', 'luottokortti', 'mobiilimaksu', 'lahimaksu',
  'lasku', 'maksusitoumus'
  -- FIXME: onko lista oikea? tarvitaanko waltti-kortti yms
);

CREATE TYPE vuokrauksenlisapalvelu AS ENUM (
  'lastenistuin','elainkuljetukset'
);

CREATE TYPE noutopaikantyyppi AS ENUM ('nouto','palautus','nouto-palautus');

CREATE TYPE valityspalvelutyyppi AS ENUM (
  'kyydinvalitys'
  -- FIXME: lista välityspalveluiden tyypeistä
);


-- Palvelutieto on linkki palvelutietoihin sekä kuvaus siitä mitä linkin takana on
CREATE TYPE palvelutietolinkki AS (
  osoite VARCHAR(1024),
  kuvaus TEXT
);

CREATE TYPE terminaalitiedot AS (
  sijainti geometry,
  aukioloajat aukioloaika[],
  sisatilakartta VARCHAR(1024), -- URL-osoite kuvaan tai sivuun
  esteettomyys esteettomyystuki[],
  esteettomyyskuvaus TEXT, -- Vapaa kuvaus esteettömyyspalveluista
  erityispalvelut erityispalvelu[],
  palvelutiedot palvelutietolinkki[]
);

CREATE TYPE toimintaalue AS (
  "alueen-kuvaus" TEXT, -- tekstikuvaus alueesta, esim. kunnan nimi
  sijainti GEOMETRY -- mahdollinen tarkempi geometriatieto
);

CREATE TYPE henkilokuljetustiedot AS (
  -- FIXME: onko vapaatekstinä taulukko ok rajoitukset vai pitääkö mallintaa?
  matkatavararajoitukset TEXT[],
  reaaliaikatiedot VARCHAR(1024), -- URL reaaliaikaiseen tietoon (esim. karttasivu)
  "paatoiminta-alueet" toimintaalue[],
  "toissijaiset-toiminta-alueet" toimintaalue[],
  varauspalvelu palvelutietolinkki -- linkki ja kuvaus nettiajanvaraukseen

  -- TODO:
  -- henkilökuljetuspalvelun tarkemmat tiedot:
  -- reitti, liikennevälineet, pysähtymispaikat, aikataulu
  -- myöhästymiset, perumiset, lisäpalvelut, hintatiedot

);


CREATE TYPE noutopaikka AS (
  nimi VARCHAR(100),
  "noutopaikan-tyyppi" noutopaikantyyppi,
  noutoajat aukioloaika[]
);


CREATE TYPE vuokraustiedot AS (
  "pyoratuolituki?" BOOLEAN, -- FIXME: onko lisäpalvelu
  kelpoisuusvaatimukset TEXT, -- tekstikuvaus vuokraajan kelpoisuusvaatimuksista
  varauspalvelu palvelutietolinkki,
  "vuokrauksen-lisapalvelut" vuokrauksenlisapalvelu[],
  noutopaikat noutopaikka[]
);



CREATE TYPE pysakointialue AS (
  alue toimintaalue,
  aukioloajat aukioloaika[],
  maksutavat maksutapa[],
  esteettomyys esteettomyystuki[],
  esteettomyyskuvaus TEXT,
  latauspisteet TEXT, -- kuvaus mahdollisista sähköajoneuvon latauspaikoista
  varauspalvelu palvelutietolinkki,
  liikennevalineet liikennevalinetyyppi[]
);

CREATE TYPE pysakointitiedot AS (
  pysakointialueet pysakointialue[]
);


CREATE TYPE valitettavapalvelu AS (
  nimi VARCHAR(100),
  kuvaus TEXT,
  "valityspalvelun-tyyppi" valityspalvelutyyppi,
  "paatoiminta-alueet" toimintaalue[],
  "toissijaiset-toiminta-alueet" toimintaalue[],
  hintatiedot palvelutietolinkki -- URL hinnoittelutietojen sivuille
  -- FIXME: dynaamiset hintatiedot, vapaa kapasiteetti, alennusperusteet?
  -- miten mallinnetaan?
);

CREATE TYPE valitystiedot AS (
  "valitettavat-palvelut" valitettavapalvelu[]
);

CREATE TABLE liikkumispalvelu (
  id SERIAL PRIMARY KEY,
  "palveluntuottaja-id" INTEGER REFERENCES palveluntuottaja (id) NOT NULL,
  tyyppi liikkumispalvelutyyppi NOT NULL,
  terminaali terminaalitiedot,
  henkilokuljetus henkilokuljetustiedot,
  vuokraus vuokraustiedot,
  pysakointi pysakointitiedot, -- FIXME: ei ole tiedossa pyskäintitietoja nyt?
  valitys valitystiedot -- FIXME: ei ole tiedossa nyt
);



-- Aikatauluja varten
