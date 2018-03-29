-- Transport type 'road' is mandatory default value for taxi services.

UPDATE "transport-service"
   SET "transport-type" = '{road}'
 WHERE "sub-type" = 'taxi';
