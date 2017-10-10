ALTER TABLE "transport-operator"
  ADD COLUMN "ckan-group-id" TEXT REFERENCES "group" (id);

ALTER TABLE "transport-service"
  ADD COLUMN created timestamp with time zone,
  ADD COLUMN "created-by" TEXT REFERENCES "user" (id),
  ADD COLUMN modified timestamp with time zone,
  ADD COLUMN "modified-by" TEXT REFERENCES "user" (id)
;

