-- Goal is to remove interface-id from external-interface-download-status and replace it with service-id
-- But to reduce risks we do not yet remove interface-id. The code is here (and it is tested) but it doesn't do anything yet. It is commented off.
-- When everything is ok in production db, then it can be used.

-- First; Add service-id
ALTER TABLE "external-interface-download-status"
    ADD COLUMN "transport-service-id" INTEGER REFERENCES "transport-service" (id) ON DELETE CASCADE ON UPDATE CASCADE;


-- Second: UPDATE all existing rows with service-id
UPDATE  "external-interface-download-status" eid
   SET "transport-service-id" = i."transport-service-id"
  FROM "external-interface-description" i
 WHERE i.id = eid."external-interface-description-id";

-- Third: Remove foreign key from external-interface-download-status
ALTER TABLE "external-interface-download-status"
    DROP CONSTRAINT "external-interface-download-s_external-interface-descripti_fkey";

-- Fourth: Remove interface-id from the table -- actually, add this to another flyway migration file.
--ALTER TABLE "external-interface-download-status"
--    DROP COLUMN "external-interface-description-id";
