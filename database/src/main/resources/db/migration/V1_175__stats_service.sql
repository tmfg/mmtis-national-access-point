-- Create table to store service statistics. At first we store only netex downloads

-- Create enum stat_type
CREATE TYPE "stat_type" AS ENUM ('download-route-netex-ui', 'download-route-netex-api');

-- Create table "stats-service"
CREATE TABLE "stats-service"
(
    id                      SERIAL PRIMARY KEY,
    "transport-service-id"  INTEGER NOT NULL REFERENCES "transport-service" (id),
    "type"                  stat_type NOT NULL,
    created                 timestamp with time zone
);