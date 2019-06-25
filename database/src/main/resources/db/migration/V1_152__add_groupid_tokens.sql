ALTER TABLE "user-token"
ADD COLUMN "ckan-group-id" TEXT REFERENCES "group" (id),
DROP COLUMN "operator-id";
