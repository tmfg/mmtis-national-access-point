ALTER TABLE gtfs_package
  ADD "external-interface-description-id" INTEGER REFERENCES "external-interface-description" (id);

UPDATE gtfs_package p
   SET "external-interface-description-id" =
       (SELECT id
          FROM "external-interface-description" eid
         WHERE 'route-and-schedule' = ANY("data-content")
           AND ('GTFS' = ANY("format") OR 'Kalkati' = ANY("format"))
           AND eid."transport-service-id" = p."transport-service-id"
         LIMIT 1);
