-- name: fetch-user-by-username
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
     WHERE u.name = :username;

-- name: list-users
SELECT u.id as id,
       u.name as username,
       u.fullname as name,
       u.email as email,
       u.sysadmin as "admin?",
       (SELECT string_agg(g.title, ', ')
          FROM "member" m
               JOIN "group" g ON g.id = m.group_id
         WHERE m.table_name='user' AND
               m.state='active' AND
               m.table_id=u.id) as groups
  FROM "user" u
 WHERE state = 'active' AND
       ((:email::VARCHAR IS NOT NULL AND u.email LIKE :email) OR
        (:name::VARCHAR IS NOT NULL AND u.fullname LIKE :name));
