-- update existing triggers and operation-area-facet.

-- delete all from operation-area-facet table
DELETE FROM "operation-area-facet";

-- insert initial data to facet. Use all areas matching rows in places-view.
INSERT
INTO "operation-area-facet"
("transport-service-id", "operation-area")
  SELECT t.id, LOWER(oad.text)
  FROM operation_area oa
    JOIN "transport-service" t ON oa."transport-service-id" = t.id
    JOIN LATERAL unnest(oa.description) AS oad ON TRUE
  WHERE t."published?" = true
        AND oad.text IN (SELECT p.namefin FROM "places" p);


-- Create trigger function to update facet when service changes
CREATE OR REPLACE FUNCTION transport_service_operation_area_array () RETURNS TRIGGER AS $$
BEGIN

  -- Delete all previous entries for this service
  DELETE
  FROM "operation-area-facet"
  WHERE "transport-service-id" = NEW.id;

  IF NEW."published?" = TRUE THEN
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
