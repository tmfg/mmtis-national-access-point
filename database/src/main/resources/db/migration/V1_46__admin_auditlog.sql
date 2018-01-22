CREATE TYPE auditlog_event_type AS ENUM ('delete-service','modify-service','add-service','delete-operator',
       'modify-operator', 'add-operator', 'delete-user', 'modify-user', 'add-user');


CREATE TYPE "auditlog-event-attribute" AS (
  "name" text,
  "value" text);

-- transport-service-id=<id
-- transport-service-name=Some deleted service

CREATE TABLE auditlog (
  id SERIAL PRIMARY KEY,
  "event-type" auditlog_event_type,
  "event-attributes" "auditlog-event-attribute"[],
  "event-timestamp" timestamp with time zone,
  "created-by" TEXT REFERENCES "user" (id)
);

