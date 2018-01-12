-- Add all-day boolean value to service_hours TYPE
ALTER TYPE service_hours
  ADD ATTRIBUTE "all-day" boolean;