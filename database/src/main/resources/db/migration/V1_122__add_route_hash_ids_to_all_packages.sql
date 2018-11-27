-- Create function that generates routes to detection-route table using default route-hash-id (short-long-headsign)
CREATE OR REPLACE FUNCTION generate_route_hash_ids_for_packages() RETURNS VOID AS $$
DECLARE
 p_id RECORD;
BEGIN

  -- Delete all
  DELETE FROM "detection-route";

  -- Generate new
  FOR p_id IN SELECT id FROM "gtfs_package"
   LOOP PERFORM
    calculate_route_hash_id_using_headsign(p_id.id);
  END LOOP;

END
$$ LANGUAGE plpgsql;

-- Call function to start the process - this might take about a minute or so.
SELECT generate_route_hash_ids_for_packages();