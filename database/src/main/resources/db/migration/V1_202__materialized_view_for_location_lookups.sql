-- Adds lookup view to quickly access the hierarchy of various region levels. Parent is the encompassing region, child
-- is the contained region, eg. Europe contains Finland and Sweden, Finland contains Kanta-Häme, Kanta-Häme contains
-- Forssa, Forssa contains 31110 Matku.
--
-- For convenience the encompassing areas also include all the areas within, so municipalities also have data for postal
-- code areas. Use parent_type and child_type to limit what you really want.

CREATE MATERIALIZED VIEW IF NOT EXISTS location_relations
AS
SELECT p.id         AS parent_id,
       p.type       AS parent_type,
       p.namefin    AS parent_namefin,
       p.nameswe    AS parent_nameswe,
       c.id         AS child_id,
       c.type       AS child_type,
       c.namefin    AS child_namefin,
       c.nameswe    AS child_nameswe
  FROM places p
           JOIN places AS c ON (ST_Within(c.location, p.location) = TRUE OR
                                ST_Overlaps(c.location, p.location) = TRUE)
 WHERE p.type = 'finnish-region'
   AND c.type IN ('finnish-municipality', 'finnish-postal')
 UNION ALL
SELECT p.id         AS parent_id,
       p.type       AS parent_type,
       p.namefin    AS parent_namefin,
       p.nameswe    AS parent_nameswe,
       c.id         AS child_id,
       c.type       AS child_type,
       c.namefin    AS child_namefin,
       c.nameswe    AS child_nameswe
  FROM places p
           JOIN places AS c ON (ST_Within(c.location, p.location) = TRUE OR
                                ST_Overlaps(c.location, p.location) = TRUE)
 WHERE p.type = 'finnish-municipality'
   AND c.type = 'finnish-postal'
WITH DATA;

CREATE INDEX location_relations_p_type ON location_relations (parent_type);
CREATE INDEX location_relations_p_namefin ON location_relations (parent_namefin);
CREATE INDEX location_relations_c_type ON location_relations (child_type);
CREATE INDEX location_relations_c_namefin ON location_relations (child_namefin);
CREATE INDEX location_relations_ptype_pnamefin_cnamefin ON location_relations (parent_type, parent_namefin, child_namefin);

CREATE FUNCTION refresh_location_relations ()
    RETURNS VOID
AS $$
BEGIN
    REFRESH MATERIALIZED VIEW "location_relations";
    RETURN;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;