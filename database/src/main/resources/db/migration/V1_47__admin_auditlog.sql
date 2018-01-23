-- Add auditlog table where all admin operations should be stored.
-- Add auditolog-event-attribute
-- Add auditlog-event-type

CREATE TYPE auditlog_event_type AS ENUM ('delete-service','modify-service','add-service','delete-operator',
       'modify-operator', 'add-operator', 'delete-user', 'modify-user', 'add-user');

CREATE TYPE "auditlog_event_attribute" AS (
  "name" text,
  "value" text);

-- name = transport-service-id, value = <id>
-- name = transport-service-name = <Some deleted service>

CREATE TABLE auditlog (
  id SERIAL PRIMARY KEY,
  "event-type" auditlog_event_type,
  "event-attributes" "auditlog_event_attribute"[],
  "event-timestamp" timestamp with time zone,
  "created-by" TEXT REFERENCES "user" (id)
);

