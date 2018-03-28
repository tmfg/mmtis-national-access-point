-- Remove subtype other and replace it with subtype request
UPDATE "transport-service"
   SET "sub-type" = 'request'
 WHERE "sub-type" = 'other';
