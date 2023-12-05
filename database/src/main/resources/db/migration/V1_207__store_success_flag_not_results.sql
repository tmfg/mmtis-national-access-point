-- Track whether the related TIS entry processing was successful.
-- The semantics of this field are vague on purpose.
ALTER TABLE "gtfs_package"
    ADD COLUMN "tis-success" BOOLEAN DEFAULT FALSE,
    DROP COLUMN "tis-result-links";