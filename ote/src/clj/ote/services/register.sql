-- name: username-exists?
-- single?: true
SELECT EXISTS(SELECT id FROM "user" WHERE LOWER(name) = LOWER(:username));

-- name: email-exists?
-- single?: true
SELECT EXISTS(SELECT id FROM "user" WHERE LOWER(email) = LOWER(:email));

-- name: fetch-operator-info
SELECT tro."business-id", tro.name, tro."ckan-group-id"
FROM "user-tokens" ut
LEFT JOIN "transport-operator" tro ON tro.id = ut."operator-id"
WHERE token = :token AND ut.expiration >= (NOW())::date AND NOT tro."deleted?"
