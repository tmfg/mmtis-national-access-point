-- name: fetch-service-business-ids
select c.name as "operator", c."business-id" as "business-id", s."contact-phone" as "phone",  s."contact-email" as "email"
from "transport-service" s
LEFT JOIN LATERAL unnest(s.companies) AS c ON TRUE
WHERE c."business-id" is not null;

-- name: fetch-operator-business-ids
select o."name" as "operator", o."business-id" as "business-id", o."phone" as "phone", o."gsm", o."email" as "email"
FROM "transport-operator" o
WHERE o."business-id" is not null;
