ALTER TABLE country
  ADD COLUMN nameeng TEXT;

CREATE TABLE continent (
  code CHAR(2),
  namefin TEXT,
  nameswe TEXT,
  nameeng TEXT,
  location geometry
);
