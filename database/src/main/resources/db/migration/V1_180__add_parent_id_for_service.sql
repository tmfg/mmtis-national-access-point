-- Add parent-id integer to transport-service table
ALTER TABLE "transport-service"
    ADD COLUMN "parent-id" INTEGER DEFAULT NULL;