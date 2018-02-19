-- Add table for daily stats about company count

CREATE TABLE company_stats (
  "date" DATE PRIMARY KEY,
  "count" INTEGER
);

CREATE OR REPLACE FUNCTION store_daily_company_stats () RETURNS VOID AS $$
INSERT INTO company_stats ("date", "count")
VALUES ((SELECT current_date - 1),
        (SELECT SUM(COALESCE(
                (SELECT array_length(companies,1)
                   FROM service_company sc
                  WHERE sc."transport-service-id" = ts.id),
                array_length(companies,1),
                0))
          FROM "transport-service" ts
         WHERE ts."published?" = TRUE))
ON CONFLICT DO NOTHING;
$$ LANGUAGE SQL;


-- Add updated timestamp to service_company

ALTER TABLE service_company ADD COLUMN updated TIMESTAMPTZ DEFAULT current_timestamp;
