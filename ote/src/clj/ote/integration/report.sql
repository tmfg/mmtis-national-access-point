-- name: fetch-import-reports-for-latest-packages
SELECT "gp".id                                  AS "gtfs-package_id",
       "gp".created                             AS "gtfs-package_created",
       "gp"."external-interface-description-id" AS "gtfs-package_external-interface-description-id",
       "to".id                                  AS "transport-operator_id",
       "to".name                                AS "transport-operator_name",
       "ts".id                                  AS "transport-service_id",
       "ts".name                                AS "transport-service_name",
       "gir".id                                 AS "gtfs-import-report_id",
       "gir".severity                           AS "gtfs-import-report_severity",
       "gir".description                        AS "gtfs-import-report_description",
       "gir".error                              AS "gtfs-import-report_error"
  FROM "gtfs_import_report" "gir"
           JOIN gtfs_package gp ON gir.package_id = gp.id
           JOIN "transport-operator" "to" ON "gp"."transport-operator-id" = "to".id
           JOIN "transport-service" "ts" ON "gp"."transport-service-id" = "ts".id
 WHERE "gir".package_id IN (SELECT DISTINCT ON ("gp"."transport-service-id") "gp".id AS "gtfs-package_id"
                              FROM gtfs_package "gp"
                             ORDER BY "gp"."transport-service-id", "gp".id DESC)

-- name: fetch-latest-import-reports-for-service
SELECT "gp".id                                  AS "gtfs-package_id",
      "gp".created                             AS "gtfs-package_created",
      "gp"."external-interface-description-id" AS "gtfs-package_external-interface-description-id",
      "to".id                                  AS "transport-operator_id",
      "to".name                                AS "transport-operator_name",
      "ts".id                                  AS "transport-service_id",
      "ts".name                                AS "transport-service_name",
      "gir".id                                 AS "gtfs-import-report_id",
      "gir".severity                           AS "gtfs-import-report_severity",
      "gir".description                        AS "gtfs-import-report_description",
      "gir".error                              AS "gtfs-import-report_error"
 FROM "gtfs_import_report" "gir"
          JOIN gtfs_package gp ON gir.package_id = gp.id
          JOIN "transport-operator" "to" ON "gp"."transport-operator-id" = "to".id
          JOIN "transport-service" "ts" ON "gp"."transport-service-id" = "ts".id
WHERE "gir".package_id IN (SELECT DISTINCT ON ("gp"."transport-service-id") "gp".id AS "gtfs-package_id"
                             FROM gtfs_package "gp"
                            WHERE "gp"."transport-service-id" = :transport-service-id
                              AND "gp"."external-interface-description-id" = :external-interface-description-id
                            ORDER BY "gp"."transport-service-id", "gp".id DESC);