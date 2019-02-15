-- Create trigger function to update facet when service changes
CREATE OR REPLACE FUNCTION transport_service_operation_area_array () RETURNS TRIGGER AS $$
BEGIN

  -- Delete all previous entries for this service
  DELETE
  FROM "operation-area-facet"
  WHERE "transport-service-id" = NEW.id;

  IF NEW.published IS NOT NULL THEN
    -- Insert new values (for published only)
    INSERT
    INTO "operation-area-facet"
    ("transport-service-id", "operation-area")
    SELECT NEW.id, LOWER(oad.text)
    FROM operation_area oa
           JOIN LATERAL unnest(oa.description) AS oad ON TRUE
    WHERE oa."transport-service-id" = NEW.id
      AND oad.text IN (SELECT p.namefin FROM "places" p);
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION store_daily_company_stats () RETURNS VOID AS $$
INSERT INTO company_stats ("date", "count")
VALUES ((SELECT current_date - 1),
        (SELECT SUM(COALESCE(
            (SELECT array_length(companies,1)
             FROM service_company sc
             WHERE sc."transport-service-id" = ts.id),
            array_length(ts.companies,1),
            0))
         FROM "transport-service" ts
         WHERE ts.published IS NOT NULL))
ON CONFLICT ("date") DO
  UPDATE SET "count" =  EXCLUDED."count";
$$ LANGUAGE SQL;
