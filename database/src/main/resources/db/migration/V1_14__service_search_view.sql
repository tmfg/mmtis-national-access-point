CREATE TABLE "operation-area-facet" (
 "transport-service-id" INTEGER REFERENCES "transport-service" (id) ON DELETE CASCADE,
 "operation-area" TEXT
);

CREATE INDEX operation_area_id_idx ON "operation-area-facet" ("transport-service-id");
CREATE INDEX operation_area_text_idx ON "operation-area-facet" ("operation-area");

CREATE FUNCTION transport_service_operation_area_array () RETURNS TRIGGER AS $$
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
    SELECT NEW.id, oad.text
      FROM operation_area oa
      JOIN LATERAL unnest(oa.description) AS oad ON TRUE
     WHERE oa."transport-service-id" = NEW.id;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE CONSTRAINT TRIGGER tg_transport_service_operation_area_array
AFTER INSERT OR UPDATE
ON "transport-service"
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE PROCEDURE transport_service_operation_area_array();


--- Create index for subtype (also used as a search facet)
CREATE INDEX transport_service_subtype_idx
    ON "transport-service" ("sub-type")
 WHERE "published?"=TRUE;


CREATE TYPE external_interface_search_result AS (
  "external-interface" service_link,
  format TEXT,
  "ckan-resource-id" TEXT
);


-- Create view for search results
CREATE VIEW transport_service_search_result AS
SELECT t.*,
       (SELECT op.name
          FROM "transport-operator" op
         WHERE op.id = t."transport-operator-id") as "operator-name",
       (SELECT array_agg(oaf."operation-area")
          FROM "operation-area-facet" oaf
         WHERE oaf."transport-service-id" = t.id) AS "operation-area-description",
       (SELECT array_agg(ROW(ei."external-interface", ei.format, ei."ckan-resource-id")::external_interface_search_result)
          FROM "external-interface-description" ei
         WHERE ei."transport-service-id" = t.id)::external_interface_search_result[] AS "external-interface-links"
  FROM "transport-service" t;
