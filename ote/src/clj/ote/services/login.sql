-- name: fetch-login-info
SELECT name, email, password, fullname
  FROM "user"
 WHERE (name = :email OR email = :email) and state = 'active';
