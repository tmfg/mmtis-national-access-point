-- Add new column 'url' to "external-interface-download-status"
ALTER TABLE "external-interface-download-status"
    ADD COLUMN "url" VARCHAR(1024);

-- Add url to old rows in "external-interface-download-status" table.
-- Get url from "external-interface-description"
UPDATE "external-interface-download-status" d SET url = (SELECT (e."external-interface").url
                                                           FROM "external-interface-description" e
                                                          WHERE e.id = d."external-interface-description-id")