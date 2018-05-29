CREATE OR REPLACE FUNCTION del_operator(operator_group_name varchar ) RETURNS BOOL language plpgsql AS $$
BEGIN

  -- Remove services under operator
  DELETE FROM "transport-service"
   WHERE "transport-operator-id" =
         (SELECT id FROM "transport-operator"
           WHERE "ckan-group-id" =
                (SELECT id FROM "group"
                  WHERE name = operator_group_name));

  -- Remove OTE operator
  DELETE FROM "transport-operator"
   WHERE "ckan-group-id" =
         (SELECT id FROM "group"
           WHERE name = operator_group_name);

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