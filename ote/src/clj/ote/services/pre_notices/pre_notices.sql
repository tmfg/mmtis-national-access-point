-- name: fetch-authority-pre-notices
SELECT p.id, p.sent, p.created, p.regions, p."route-description", p."pre-notice-type" as "pre-notice-type",
       (select array_agg(ed."effective-date") from unnest(p."effective-dates") ed) as "change-dates",
       top.name as "transport-operator"
  FROM "pre_notice" p, "transport-operator" top
 WHERE top.id = p."transport-operator-id"
 ORDER BY p."effective-dates"[1] DESC;