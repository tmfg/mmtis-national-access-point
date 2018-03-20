-- Add realtime information for rental
ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "real-time-information" service_link;
