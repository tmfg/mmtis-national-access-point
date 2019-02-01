ALTER TABLE "associated-service-operators"
  ADD COLUMN timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN "user-id" TEXT REFERENCES "user" (id);
