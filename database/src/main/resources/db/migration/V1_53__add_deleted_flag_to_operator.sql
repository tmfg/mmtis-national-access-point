-- Add deleted? boolean flag to transport-operator
ALTER TABLE "transport-operator"
   ADD COLUMN "deleted?" boolean default false;

-- Update deleted? status according to ckan group state
UPDATE "transport-operator" as o
   SET "deleted?" = TRUE
 WHERE o.id IN (SELECT o.id
                  FROM "transport-operator" as o, "group" as g
                 WHERE o."ckan-group-id" = g.id
                   AND g.state = 'deleted');
