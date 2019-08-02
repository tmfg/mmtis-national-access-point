-- We need to store interface download state (success, failure) somewhere.
-- This table is reserved for that.

-- Create state enum
CREATE TYPE interface_download_status AS ENUM ('success','failure');

-- And the table
CREATE TABLE "interface-download" (
  id SERIAL PRIMARY KEY,
  "external-interface-description-id"  INTEGER REFERENCES "external-interface-description" (id) NOT NULL,
  "download-error" text,
  "db-error" text,
  "download-status" interface_download_status,
  created TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);