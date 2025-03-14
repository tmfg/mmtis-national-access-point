-- name: store-daily-company-stats
SELECT store_daily_company_stats();

-- name: select-company-csv-for-update
SELECT "transport-service-id" FROM service_company sc, "transport-service" ts
 WHERE sc.updated < (current_timestamp - '1 day'::interval)
   AND ts.id = sc."transport-service-id"
   AND ts.published IS NOT NULL
   AND sc.source = 'URL'
ORDER BY sc.updated ASC LIMIT 1
 FOR UPDATE SKIP LOCKED;

-- name: fetch-company-csv-url
-- single?: true
SELECT "companies-csv-url"
  FROM "transport-service"
 WHERE id = :transport-service-id;

-- name: update-done!
UPDATE service_company
   SET updated = current_timestamp
 WHERE "transport-service-id" = :transport-service-id;

-- name: fetch-re-edit-services
SELECT id
  FROM "transport-service" ts
 WHERE ts."re-edit" < NOW() - interval '1 day';