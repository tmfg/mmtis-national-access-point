CREATE OR REPLACE FUNCTION flyway_after_migrate () RETURNS VOID AS $$
DECLARE
  r RECORD;
  sql TEXT;
BEGIN
  FOR r IN SELECT c.relname FROM pg_catalog.pg_class c JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace WHERE c.relowner=(SELECT usesysid FROM pg_catalog.pg_user WHERE usename='flyway') AND c.relkind IN ('r','v','S','m') AND n.nspname = current_schema()
  LOOP
    sql := 'GRANT ALL PRIVILEGES ON "' || r.relname || '" TO ote';
    EXECUTE sql;
  END LOOP;
  RETURN;
END;
$$ LANGUAGE plpgsql;

SELECT flyway_after_migrate();
