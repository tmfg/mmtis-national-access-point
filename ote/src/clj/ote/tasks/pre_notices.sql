-- name: fetch-pre-notices-by-interval
SELECT id, "pre-notice-type", "route-description", created, modified, description, regions as "region-ids",
  (SELECT array_agg(eds."effective-date" ORDER BY eds."effective-date"::DATE ASC)
     FROM unnest(n."effective-dates") eds) as "effective-dates-asc",
  (SELECT array_agg(fr.nimi)
    FROM "finnish_regions" fr
   WHERE n.regions IS NOT NULL AND fr.numero = ANY(n.regions)) as "regions",
  (SELECT op.name
     FROM "transport-operator" op
    WHERE op.id = n."transport-operator-id") as "operator-name"
  FROM "pre_notice" n
 WHERE "pre-notice-state" = 'sent' AND
       sent IS NOT NULL AND
       (sent > (current_timestamp - :interval::interval));

-- name: fetch-pre-notices-by-interval-and-regions
SELECT id, "pre-notice-type", "route-description", created, modified, description, regions as "region-ids",
  (SELECT array_agg(eds."effective-date" ORDER BY eds."effective-date"::DATE ASC)
     FROM unnest(n."effective-dates") eds) as "effective-dates-asc",
  (SELECT array_agg(fr.nimi)
    FROM "finnish_regions" fr
   WHERE n.regions IS NOT NULL AND fr.numero = ANY(n.regions)) as "regions",
  (SELECT op.name
     FROM "transport-operator" op
    WHERE op.id = n."transport-operator-id") as "operator-name"
  FROM "pre_notice" n
 WHERE "pre-notice-state" = 'sent'
   AND sent IS NOT NULL
   AND ((:regions::int[])[1] IS NULL OR (:regions::int[]) && n.regions::integer[])
   AND (sent > (current_timestamp - :interval::interval));
