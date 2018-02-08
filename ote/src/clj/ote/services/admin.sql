-- name: fetch-service-business-ids
   SELECT c.name as "operator", c."business-id" as "business-id",
          s."contact-phone" as "phone", s."contact-email" as "email"
     FROM "transport-service" s
LEFT JOIN LATERAL unnest(s.companies) AS c ON TRUE
    WHERE c."business-id" IS NOT NULL
      AND s."published?" = TRUE;

-- name: fetch-operator-business-ids
SELECT o."name" as "operator", o."business-id" as "business-id",
       o."phone" as "phone", o."email" as "email"
  FROM "transport-operator" o
 WHERE o."business-id" IS NOT NULL;
