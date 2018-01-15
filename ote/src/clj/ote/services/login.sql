-- name: fetch-login-info
SELECT name, email, password, fullname
  FROM "user"
 WHERE email = :email;
