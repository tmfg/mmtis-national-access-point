ALTER TYPE rental_provider_information
 DROP ATTRIBUTE "service-hours-info";

ALTER TYPE pick_up_location
  ADD ATTRIBUTE "service-hours-info" localized_text[];
