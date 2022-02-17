-- name: fetch-user-by-id
    SELECT u.id as user_id,
           u.name as user_username,
           u.fullname as user_name,
           u.apikey as user_apikey,
           u.email as user_email,
           u.sysadmin as "user_admin?",
           u."seen-tos?" as "user_seen-tos?",
           u."accepted-tos?" as "user_accepted-tos?",
           g.id as group_id,
           g.name as group_name,
           g.title as group_title,
           (SELECT EXISTS(SELECT ge.id FROM group_extra ge WHERE ge.key='transit-authority?' AND ge.group_id = g.id)) as "group_transit-authority?",
           (SELECT EXISTS(SELECT ge.id FROM group_extra ge WHERE ge.key='authority-group-admin?' AND ge.group_id = g.id)) as "group_authority-group-admin?"
      FROM "user" u
 LEFT JOIN "member" m ON (m.table_name='user' AND m.state='active' AND m.table_id=u.id)
 LEFT JOIN "group" g ON g.id = m.group_id
     WHERE u.id = :user-id;

-- name: fetch-user-by-email
SELECT u.id as user_id,
       u.name as user_username,
       u.fullname as user_name,
       u.apikey as user_apikey,
       u.email as user_email,
       u.sysadmin as "user_admin?",
       g.id as group_id,
       g.name as group_name,
       g.title as group_title
FROM "user" u
       LEFT JOIN "member" m ON (m.table_name='user' AND m.state='active' AND m.table_id=u.id)
       LEFT JOIN "group" g ON g.id = m.group_id
WHERE u.state = 'active'
  AND u.email IS NOT NULL
  AND u.email = :email;

-- name: list-users
SELECT u.id as id,
       u.name as username,
       u.fullname as name,
       u.email as email,
       u.sysadmin as "admin?",
       (SELECT to_json(array_agg(json_build_object('title', g.title, 'name', g.name)))
          FROM "member" m
          JOIN "group" g ON g.id = m.group_id
         WHERE m.table_name='user' AND
               m.state='active' AND
               m.table_id=u.id) as groups
  FROM "user" u
  WHERE state = 'active' AND
        ((:email :: VARCHAR IS NOT NULL AND u.email ILIKE :email) OR
         (:name :: VARCHAR IS NOT NULL AND u.fullname ILIKE :name) OR
         (:transit-authority? :: BOOLEAN IS NOT NULL AND
          EXISTS(SELECT m.group_id,m.table_id
                   FROM "member" m
                  WHERE m.table_name = 'user' AND
                        m.state = 'active' AND
                        m.table_id = u.id AND
                        m.group_id IN (SELECT ge.group_id
                                         FROM group_extra ge
                                        WHERE ge.key = 'transit-authority?' AND
                                              ge.value = :transit-authority? :: VARCHAR))) OR
         (:group :: VARCHAR IS NOT NULL AND
          EXISTS(SELECT m.group_id
                   FROM "member" m
                  WHERE m.table_name = 'user' AND
                        m.state = 'active' AND
                        m.table_id = u.id AND
                        m.group_id IN (SELECT g.id
                                         FROM "group" g
                                        WHERE g.title ILIKE :group))));

-- name: list-authority-users
SELECT u.id as id,
       u.name as username,
       u.fullname as name,
       u.email as email,
       u.sysadmin as "admin?",
       un."finnish-regions"
  FROM "user" u
  LEFT JOIN user_notifications un ON u.id = un."created-by"
 WHERE state = 'active'
   AND EXISTS(SELECT m.group_id,m.table_id
                FROM "member" m
               WHERE m.table_name = 'user'
                 AND m.state = 'active'
                 AND m.table_id = u.id
                 AND m.group_id IN (SELECT ge.group_id
                                      FROM group_extra ge
                                     WHERE ge.key = 'transit-authority?'
                                       AND ge.value = 'true'));

-- name: has-group-attribute?
-- single?: true
-- Given a user id and group attribute check if the user has the trait through any group membership.
SELECT EXISTS(SELECT ge.id
                FROM group_extra ge
               WHERE ge.key=:group-attribute AND
                     ge.value='true' AND
                     ge.group_id IN (SELECT m.group_id
                                       FROM "member" m
                                      WHERE m.table_name = 'user' AND
                                            m.state = 'active' AND
                                            m.table_id = (SELECT u.id FROM "user" u WHERE u.id = :user-id)));

-- name: transit-authority-group-id
-- single?: true
SELECT group_id
  FROM group_extra
 WHERE key = 'transit-authority?'
   AND value = 'true';

-- name: authority-group-admin-id
-- single?: true
SELECT group_id
  FROM group_extra
 WHERE key = 'authority-group-admin?'
   AND value = 'true';

-- name: search-user-operators-and-members
-- Find users operators and their members and get only admin users.
SELECT g.title as "operator-name",
 (SELECT array_agg(m.table_id) FROM member m
   WHERE m.group_id = g.id
     AND m.capacity = 'admin'
     AND m.state = 'active'
     AND m.table_id != :user-id) as "members"
 FROM "member" m, "group" g
WHERE m.table_id = :user-id
AND g.id = m.group_id
AND m.state = 'active';

-- name: delete-user!
-- "Delete" user from the database - only changes user data to null to preserve foreign key links
UPDATE "user" SET name = :name, fullname = NULL, email = :id, state = 'deleted' WHERE id = :id;
