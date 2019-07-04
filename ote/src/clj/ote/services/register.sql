-- name: username-exists?
-- single?: true
SELECT EXISTS(SELECT id FROM "user" WHERE LOWER(name) = LOWER(:username));

-- name: email-exists?
-- single?: true
SELECT EXISTS(SELECT id FROM "user" WHERE LOWER(email) = LOWER(:email));

-- name: fetch-operator-info
SELECT g.id as "ckan-group-id", g.title as name, ut.token, ut.expiration, tro."business-id"
FROM "user-token" ut
LEFT JOIN "group" g ON g.id = ut."ckan-group-id"
LEFT JOIN "transport-operator" tro on g.id = tro."ckan-group-id"
WHERE token = :token AND ut.expiration >= (NOW())::date AND tro."deleted?" IS NOT TRUE
