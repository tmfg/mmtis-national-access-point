CREATE TABLE rental_booking
(
    id                      SERIAL PRIMARY KEY,
    "transport-service-id"  INTEGER UNIQUE NOT NULL REFERENCES "transport-service" (id),
    "application-link"      TEXT,
    "phone-countrycode"     TEXT NOT NULL DEFAULT '+358',
    "phone-number"          TEXT
)