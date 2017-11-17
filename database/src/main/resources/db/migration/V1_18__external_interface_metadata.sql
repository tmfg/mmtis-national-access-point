-- Add more metadata to external interface

ALTER TABLE "external-interface-description"
  -- The name or description of the license and a possible link to it
  ADD COLUMN license TEXT,
  ADD COLUMN "license-url" VARCHAR(1024);
