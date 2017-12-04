ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "luggage-restrictions" localized_text[],
  ADD ATTRIBUTE "payment-methods" payment_method[];

ALTER TYPE vehicle_accessibility
  ADD VALUE 'disability-adapted-vehicle'; 

ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "guaranteed-vehicle-accessibility" vehicle_accessibility[],
  ADD ATTRIBUTE "limited-vehicle-accessibility" vehicle_accessibility[],
  ADD ATTRIBUTE "limited-accessibility-description" localized_text[],
  ADD ATTRIBUTE "guaranteed-accessibility-description" localized_text[];
