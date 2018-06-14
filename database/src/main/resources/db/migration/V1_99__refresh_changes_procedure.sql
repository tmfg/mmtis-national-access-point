CREATE FUNCTION refresh_nightly_transit_changes ()
RETURNS VOID
AS $$
BEGIN
 REFRESH MATERIALIZED VIEW "nightly-transit-changes";
 RETURN;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
