-- Add assistance place description and boolean flag if assistance available for customer by reservation only
ALTER TYPE assistance_info
  ADD ATTRIBUTE "assistance-place-description" localized_text[],
  ADD ATTRIBUTE "assistance-by-reservation-only" boolean;