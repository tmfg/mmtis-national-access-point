-- name: fetch-authority-pre-notices
SELECT p.id, p.sent, p.created, p.regions, p."route-description", p."pre-notice-type" AS "pre-notice-type",
       (SELECT array_agg(ed."effective-date") FROM unnest(p."effective-dates") ed) AS "change-dates",
       top.name AS "transport-operator"
  FROM "pre_notice" p, "transport-operator" top
 WHERE top.id = p."transport-operator-id"
   AND p."sent" IS NOT NULL
   AND p."pre-notice-state" = 'sent'
 ORDER BY p.sent DESC;