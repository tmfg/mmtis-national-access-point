
-- R file is handled after these V files. So We need to add this function twice once in here on once to R file
CREATE OR REPLACE FUNCTION calculate_route_hash_id_using_headsign(package_id INTEGER)
RETURNS VOID
AS $$
BEGIN

  DELETE FROM "detection-route" WHERE "package-id" = package_id;

  INSERT INTO "detection-route" ("gtfs-route-id", "package-id", "route-id", "route-short-name", "route-long-name", "route-hash-id", "trip-headsign")
    SELECT r.id, r."package-id", r."route-id", r."route-short-name", r."route-long-name",
           concat(trim(r."route-short-name"), '-', trim(r."route-long-name"), '-', trim(trip."trip-headsign")), trip."trip-headsign"
     FROM "gtfs-route" r
     JOIN "gtfs-trip" t ON (t."package-id" = r."package-id" AND r."route-id" = t."route-id")
     JOIN LATERAL unnest(t.trips) trip ON true
    WHERE r."package-id" = package_id
    GROUP BY r.id, trip."trip-headsign";

END
$$ LANGUAGE plpgsql;



-- Create function that generates routes to detection-route table using default route-hash-id (short-long-headsign)
CREATE OR REPLACE FUNCTION generate_route_hash_ids_for_packages() RETURNS VOID AS $$
DECLARE
 p_id RECORD;
BEGIN

  -- Generate new route-hash-ids for all packages
  FOR p_id IN SELECT id FROM "gtfs_package"
   LOOP PERFORM
    calculate_route_hash_id_using_headsign(p_id.id);
  END LOOP;

END
$$ LANGUAGE plpgsql;

-- Call function to start the process - this might take about a minute or so.
SELECT generate_route_hash_ids_for_packages();