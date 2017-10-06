-- name: fetch-user-by-username
SELECT u.id as user_id,
       u.name as user_username,
       u.fullname as user_name,
       u.email as user_email,
       g.id as group_id,
       g.name as group_name,
       g.title as group_title
  FROM "user" u
  JOIN "member" m ON (m.table_name='user' AND m.state='active' AND m.table_id=u.id)
  JOIN "group" g ON g.id = m.group_id
 WHERE u.name = :username;
