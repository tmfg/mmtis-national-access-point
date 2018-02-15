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
       AND oad.text IN (SELECT f.namefin FROM "finnish_municipalities" f);
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Change all operation areas to lower case
UPDATE "operation-area-facet" SET "operation-area"=LOWER("operation-area");
