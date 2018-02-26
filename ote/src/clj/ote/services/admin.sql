-- name: fetch-service-business-ids
   SELECT c.name as "operator", c."business-id" as "business-id",
          s."contact-phone" as "phone", s."contact-gsm" as "gsm", s."contact-email" as "email", 1 as "source"
     FROM "transport-service" s
          LEFT JOIN service_company sc ON sc."transport-service-id" = s.id
          LEFT JOIN LATERAL unnest(COALESCE(sc.companies, s.companies)) AS c ON TRUE
    WHERE c."business-id" IS NOT NULL
      AND s."published?" = TRUE;

-- name: fetch-operator-business-ids
SELECT o."name" as "operator", o."business-id" as "business-id",
       o."phone" as "phone", o."gsm" as "gsm", o."email" as "email", 2 as "source"
  FROM "transport-operator" o
 WHERE o."business-id" IS NOT NULL;
