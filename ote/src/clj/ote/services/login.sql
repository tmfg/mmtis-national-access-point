-- name: fetch-login-info
SELECT name, email, password, fullname, id, "email-confirmed?"
  FROM "user"
 WHERE (lower(name) = lower(:email) OR lower(email) = lower(:email)) and state = 'active';
