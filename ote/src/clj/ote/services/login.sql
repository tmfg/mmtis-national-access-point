-- name: fetch-login-info
SELECT name, email, password, fullname
  FROM "user"
 WHERE (name = :email OR email = :email) and state = 'active';

-- name: username-exists?
-- single?: true
SELECT EXISTS(SELECT id FROM "user" WHERE name = :username);

-- name: email-exists?
-- single?: true
SELECT EXISTS(SELECT id FROM "user" WHERE email = :email);
