-- name: fetch-login-info
SELECT name, email, password, fullname, id
  FROM "user"
 WHERE (lower(name) = lower(:email) OR lower(email) = lower(:email)) and state = 'active';

