CREATE OR REPLACE FUNCTION del_operator(operator_group_name varchar ) RETURNS BOOL language plpgsql AS $$
DECLARE
  op INTEGER;
BEGIN
  -- Fetch corresponding OTE transport-operator id for CKAN group name
  SELECT id
    FROM "transport-operator"
    INTO op
   WHERE "ckan-group-id" = (SELECT id FROM "group" WHERE name = operator_group_name);

  -- Remove service stats under services managed by operator
  DELETE FROM "stats-service"
   WHERE "transport-service-id" IN (SELECT id
                                      FROM "transport-service"
                                     WHERE "transport-operator-id" = op);

  -- Remove services under operator
  DELETE FROM "transport-service" WHERE "transport-operator-id" = op;

  -- Set pre notice references in attachments to NULL
  -- we want to keep track of them so that we can delete orphaned
  -- files from S3
  UPDATE pre_notice_attachment
     SET "pre-notice-id" = NULL
   WHERE "pre-notice-id" IN (SELECT id
                               FROM pre_notice
                              WHERE "transport-operator-id" = op);
  -- Remove pre notices
  DELETE FROM pre_notice WHERE "transport-operator-id" = op;

  -- Remove sea routes
  DELETE FROM transit_route WHERE "transport-operator-id" = op;

  -- Remove OTE operator
  DELETE FROM "transport-operator" WHERE id = op;

  -- Delete member from member_revision
  DELETE FROM member_revision
   WHERE group_id =
         (SELECT id FROM "group"
           WHERE name = operator_group_name);

  -- Delete member row
  DELETE FROM member
   WHERE group_id =
         (SELECT id FROM "group"
           WHERE name = operator_group_name);

  -- Remove organization from group_revision
  DELETE FROM group_revision
   WHERE continuity_id =
         (SELECT id FROM "group"
           WHERE name = operator_group_name);

  -- Remove organization from table group
  DELETE FROM "group"
   WHERE name = operator_group_name;

  RETURN TRUE;
END $$;
