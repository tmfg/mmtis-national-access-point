CREATE OR REPLACE FUNCTION flyway_after_migrate () RETURNS VOID AS $$
DECLARE
  r RECORD;
  sql TEXT;
BEGIN
  FOR r IN SELECT relname FROM pg_catalog.pg_class WHERE relowner=(SELECT usesysid FROM pg_catalog.pg_user WHERE usename='flyway') AND relkind IN ('r','v','S')
  LOOP
    sql := 'GRANT ALL PRIVILEGES ON "' || r.relname || '" TO ote';
    EXECUTE sql;
  END LOOP;
  RETURN;
END;
$$ LANGUAGE plpgsql;

SELECT flyway_after_migrate();
