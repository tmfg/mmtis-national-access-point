-- Add advance reservation type in services
CREATE TYPE advance_reservation AS ENUM ('no', 'possible', 'mandatory');

ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "advance-reservation" advance_reservation;

ALTER TYPE passenger_transportation_info
  ADD ATTRIBUTE "advance-reservation" advance_reservation;

ALTER TYPE parking_provider_information
  ADD ATTRIBUTE "advance-reservation" advance_reservation;
