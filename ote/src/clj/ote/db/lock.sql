-- name: acquire-lock
-- single?: true
SELECT acquire_lock(:id, :lock, :timelimit);

-- name: release-lock
-- single?: true
SELECT release_lock(:id);
