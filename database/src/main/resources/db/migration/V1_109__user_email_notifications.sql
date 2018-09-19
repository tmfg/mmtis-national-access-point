-- Create table user-notification-regions
ALTER TABLE "finnish_regions"
  ADD CONSTRAINT unique_numero UNIQUE (numero);

CREATE TABLE user_notifications (
  "created-by" TEXT REFERENCES "user" (id) PRIMARY KEY,
  "finnish-regions" CHAR(2)[],
  "service-changed-6-months-ago" BOOLEAN,
  created TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  modified TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);