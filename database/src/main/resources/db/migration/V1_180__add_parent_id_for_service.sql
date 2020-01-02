-- Add parent-id integer to transport-service table
ALTER TABLE "transport-service"
    ADD COLUMN "parent-id" INTEGER DEFAULT NULL;

-- Make foreign key deferrable to enable conflicted state
ALTER TABLE "external-interface-description" ALTER CONSTRAINT "external-interface-description_transport-service-id_fkey" DEFERRABLE;

CREATE OR REPLACE FUNCTION child_parent_interfaces(parent_id INTEGER, child_id INTEGER) RETURNS VOID AS $$
DECLARE
    unused_id INTEGER;
BEGIN

    -- Find next unused transport-service-id and give it some gap
    SELECT i.id + 9999 as id
    INTO unused_id
    FROM "transport-service" i ORDER BY i.id DESC LIMIT 1;

    -- Change parent interfaces to belong some service that doesn't exist
    SET CONSTRAINTS ALL DEFERRED;
    UPDATE "external-interface-description" SET "transport-service-id" = unused_id WHERE "transport-service-id" = parent_id;

    -- Move child interfaces to parent
    UPDATE "external-interface-description" SET "transport-service-id" = parent_id WHERE "transport-service-id" = child_id;

    -- Move unused interfaces to child - they are deleted when child is deleted.
    UPDATE "external-interface-description" SET "transport-service-id" = child_id WHERE "transport-service-id" = unused_id;

END
$$ LANGUAGE plpgsql;