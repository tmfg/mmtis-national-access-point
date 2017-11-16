-- Add support for multiple companies

CREATE TYPE company AS (
  name VARCHAR(200),
  "business-id" CHAR(9)
);

ALTER TABLE "transport-service"
  ADD COLUMN companies company[];
