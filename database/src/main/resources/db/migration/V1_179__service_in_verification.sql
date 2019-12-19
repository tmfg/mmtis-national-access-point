-- Add validate timestamp to transport-service table
ALTER TABLE "transport-service"
    ADD COLUMN "validate" TIMESTAMP WITH TIME ZONE,
    ADD COLUMN "re-edit" TIMESTAMP WITH TIME ZONE;