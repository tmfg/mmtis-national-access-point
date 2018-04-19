-- name: fetch-pre-notices-by-interval
SELECT regions, "pre-notice-type", "route-description", created, modified, "transport-operator-id"
  FROM "pre_notice"
 WHERE "pre-notice-state" = 'sent' AND
       (created > (current_timestamp - :interval::interval) OR modified > (current_timestamp - :interval::interval));