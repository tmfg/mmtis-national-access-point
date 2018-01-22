-- Add service hours info to service types
ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "service-hours-info" localized_text[];

ALTER TYPE passenger_transportation_info
  ADD ATTRIBUTE "service-hours-info" localized_text[];

ALTER TYPE terminal_information
  ADD ATTRIBUTE "service-hours-info" localized_text[];

ALTER TYPE brokerage_service
  ADD ATTRIBUTE "service-hours-info" localized_text[];
