-- this was originally already in place during development for v208, but git sync managed to overwrite it
ALTER TABLE rental_booking
 DROP CONSTRAINT "rental_booking_transport-service-id_fkey",
  ADD CONSTRAINT "rental_booking_transport-service-id_fkey"
      FOREIGN KEY ("transport-service-id") REFERENCES "transport-service" (id)
               ON DELETE CASCADE;