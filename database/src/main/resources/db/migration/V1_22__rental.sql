ALTER TYPE pick_up_location
  ADD ATTRIBUTE "pick-up-address" address;

ALTER TYPE pick_up_location
 RENAME ATTRIBUTE "pick-up-times" TO "service-hours";

ALTER TABLE "transport-service"
RENAME COLUMN "rental" TO "rentals";
