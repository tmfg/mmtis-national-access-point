-- helper functions for creating new groups while maintaining the CKAN compatible metadata

-- Create a new group and set given array of users as members to it. All users will be set as administrators.
DROP FUNCTION IF EXISTS create_group(text,text,text,text[]);
CREATE OR REPLACE FUNCTION create_group(
    IN group_name TEXT,
    IN group_title TEXT,
    IN group_description TEXT,
    IN group_member_emails TEXT[],
    OUT revision_uuid TEXT,
    OUT group_uuid TEXT,
    OUT members_assigned TEXT[])
    LANGUAGE plpgsql AS
$func$
BEGIN
    -- 1) revision is needed by legacy CKAN table structure
       INSERT INTO "revision" ("id", "timestamp", "author", "message", "state", "approved_timestamp")
       VALUES (gen_random_uuid()::TEXT, NOW(), E'admin', E'Luo objekti ' || group_name, E'active', NULL)
    RETURNING id INTO revision_uuid;

    -- 2a) create the group
       INSERT INTO "group" ("id", "name", "title", "description", "created", "state", "revision_id", "type",
                            "approval_status", "image_url", "is_organization")
       VALUES (gen_random_uuid()::TEXT,
               group_name,
               group_title,
               group_description,
               NOW(),
               E'active',
               revision_uuid,
               E'organization',
               E'approved',
               E'',
               TRUE)
    RETURNING id INTO group_uuid;  -- this works for queries returning a single row

    -- 2b) revision link, CKAN legacy
    INSERT INTO "group_revision" ("id", "name", "title", "description", "created", "state", "revision_id",
                                  "continuity_id", "expired_id", "revision_timestamp", "expired_timestamp",
                                  "current", "type", "approval_status", "image_url", "is_organization")
    VALUES (group_uuid,
            group_name,
            group_title,
            group_description,
            NOW(),
            E'active',
            revision_uuid,
            group_uuid,
            NULL,
            NOW(),
            E'9999-12-31 00:00:00',
            NULL,
            E'organization',
            E'approved',
            E'',
            TRUE);

    -- 3) add given list of users as members to the newly created group
      WITH member_inserts AS (
          INSERT INTO "member" ("id", "table_id", "group_id", "state", "revision_id", "table_name", "capacity")
              SELECT gen_random_uuid()::TEXT,
                     id,
                     group_uuid,
                     'active',
                     revision_uuid,
                     'user',
                     'admin'
                FROM "user"
               WHERE email = ANY (group_member_emails)
              RETURNING id)
    SELECT ARRAY_AGG(id)
      INTO members_assigned
      FROM member_inserts;

    -- check all users were assigned, if not raise an exception to cancel everything this function did
    IF ARRAY_LENGTH(group_member_emails, 1) != ARRAY_LENGTH(members_assigned, 1)
    THEN
        RAISE EXCEPTION 'Not all given users were assigned to group as its members! Rolling back... (emails %, members %)',
            ARRAY_LENGTH(group_member_emails, 1),
            ARRAY_LENGTH(members_assigned, 1);
    END IF;
END
$func$;

-- Create group_extra metadata for given group.
CREATE OR REPLACE FUNCTION create_group_extra(group_uuid UUID, extra_key TEXT, extra_value TEXT DEFAULT E'true') RETURNS BOOL
    LANGUAGE plpgsql AS
$func$
BEGIN
    INSERT INTO "group_extra" ("id", "group_id", "key", "value", "state", "revision_id")
    VALUES ((SELECT name FROM "group" g WHERE g.id = group_uuid::text) || '-extra',
            group_uuid,
            extra_key,
            extra_value,
            NULL,
            NULL);
    RETURN TRUE;
END
$func$;

