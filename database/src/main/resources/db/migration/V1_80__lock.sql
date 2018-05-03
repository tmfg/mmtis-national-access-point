-- Explicit lock for cluster wide access

CREATE TABLE "lock" (
  id VARCHAR(30) PRIMARY KEY,
  "lock"    CHAR(36),
  locked  TIMESTAMP
);

CREATE OR REPLACE FUNCTION acquire_lock(lock_id VARCHAR(30), new_lock CHAR(36), timelimit BIGINT)
  RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
  found_id VARCHAR(30);
  current_lock CHAR(36);
  locked_at TIMESTAMP;
BEGIN
  LOCK TABLE "lock" IN ACCESS EXCLUSIVE MODE;
  SELECT id, "lock", locked
  INTO found_id, current_lock, locked_at
  FROM "lock"
  WHERE id = lock_id;

  IF found_id IS NULL
  THEN
    INSERT INTO "lock" (id, "lock", locked) VALUES (lock_id, new_lock, now());
    RETURN TRUE;
  ELSE
    IF current_lock IS NULL OR
       (timelimit IS NOT NULL AND (EXTRACT(EPOCH FROM (current_timestamp - locked_at)) > timelimit))
    THEN
      UPDATE "lock"
      SET "lock" = new_lock, locked = now()
      WHERE id = lock_id;
      RETURN TRUE;
    ELSE
      RETURN FALSE;
    END IF;
  END IF;
  RETURN FALSE;
END
$$;


-- Lukon avaus
CREATE OR REPLACE FUNCTION release_lock(lock_id VARCHAR(30))
  RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
  found_id VARCHAR(30);
BEGIN
  SELECT id INTO found_id FROM "lock" WHERE id = lock_id;

  IF found_id IS NULL
  THEN
    RETURN FALSE;
  ELSE
    UPDATE "lock" SET locked = NULL, "lock" = NULL
     WHERE id = lock_id;
    RETURN TRUE;
  END IF;
  RETURN FALSE;
END
$$;
