-- Set collation of transport operator and service name to "fi_FI"

-- Locally the Docker pg doesn't have finnish locales supported (AWS RDS does)
-- just create a dummy with the fi_FI name
CREATE FUNCTION create_fi_FI_collation () RETURNS VOID AS $$
BEGIN
  IF (SELECT COUNT(collname) FROM pg_collation WHERE collname='fi_FI.utf8') = 0 THEN
    EXECUTE 'CREATE COLLATION "fi_FI.utf8" FROM ucs_basic';
  END IF;
END;
$$ LANGUAGE plpgsql;

SELECT create_fi_FI_collation();

DROP FUNCTION create_fi_FI_collation ();

-- Drop depending view
DROP VIEW transport_service_search_result;

ALTER TABLE "transport-operator"
ALTER COLUMN name TYPE VARCHAR(200) COLLATE "fi_FI.utf8";

ALTER TABLE "transport-service"
ALTER COLUMN name TYPE VARCHAR(200) COLLATE "fi_FI.utf8";

-- Recreate transport_service_search_result
CREATE VIEW transport_service_search_result AS
  SELECT t.*,
    (SELECT op.name
     FROM "transport-operator" op
     WHERE op.id = t."transport-operator-id") as "operator-name",
    (SELECT array_agg(oaf."operation-area")
     FROM "operation-area-facet" oaf
     WHERE oaf."transport-service-id" = t.id) AS "operation-area-description",
    (SELECT array_agg(ROW(ei."external-interface", ei.format,
                      ei."ckan-resource-id",
                      ei.license, ei."data-content")::external_interface_search_result)
     FROM "external-interface-description" ei
     WHERE ei."transport-service-id" = t.id)::external_interface_search_result[] AS "external-interface-links"
  FROM "transport-service" t;
