-- name: fetch-login-info
SELECT name, email, password, fullname, id
  FROM "user"
 WHERE (lower(name) = lower(:email) OR lower(email) = lower(:email)) and state = 'active';

-- name: username-exists?
-- single?: true
SELECT EXISTS(SELECT id FROM "user" WHERE LOWER(name) = LOWER(:username));

-- name: email-exists?
-- single?: true
SELECT EXISTS(SELECT id FROM "user" WHERE LOWER(email) = LOWER(:email));
