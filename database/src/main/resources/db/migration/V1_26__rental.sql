ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "luggage-restrictions" localized_text[];

ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "payment-methods" payment_method[];

