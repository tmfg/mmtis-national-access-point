-- name: try-advisory-xact-lock!
-- single?: true
SELECT pg_try_advisory_xact_lock(:id::BIGINT);
