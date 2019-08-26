ALTER TABLE "external-interface-download-status"
    DROP CONSTRAINT "external-interface-download-s_external-interface-descripti_fkey";

ALTER TABLE "external-interface-download-status"
    ADD CONSTRAINT "external-interface-download-s_external-interface-descripti_fkey"
        FOREIGN KEY ("external-interface-description-id") REFERENCES "external-interface-description"
            ON DELETE CASCADE;
