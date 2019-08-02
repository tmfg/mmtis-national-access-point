-- name: email-exists?
-- single?: true
SELECT EXISTS(SELECT id FROM "user" WHERE LOWER(email) = LOWER(:email));
