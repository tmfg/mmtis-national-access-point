-- Create table for services that have different route hash id generation type than default short-long-headsign
CREATE TABLE "detection-service-route-type"
(
   id                     SERIAL PRIMARY KEY,
  "transport-service-id"  INTEGER NOT NULL REFERENCES "transport-service" (id),
  "route-hash-id-type"    text
);