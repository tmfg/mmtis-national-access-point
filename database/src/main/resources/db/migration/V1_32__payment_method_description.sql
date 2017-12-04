-- ADD payment_method_description to passenger_transportation_info and parking_area

ALTER TYPE passenger_transportation_info
  ADD ATTRIBUTE "payment-method-description" localized_text[];

ALTER TYPE PARKING_PROVIDER_INFORMATION
  ADD ATTRIBUTE "payment-method-description" localized_text[];

