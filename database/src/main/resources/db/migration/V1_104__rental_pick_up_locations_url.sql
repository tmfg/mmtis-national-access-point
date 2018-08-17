-- Add 'pick-up-locations-url' to rental_provider_information
ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "pick-up-locations-url" VARCHAR(1024);

