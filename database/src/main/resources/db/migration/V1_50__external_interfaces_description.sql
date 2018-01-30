CREATE TYPE interface_data_content AS ENUM (
  'route-and-schedule',
  'luggage-restrictions',
  'realtime-interface',
  'booking-interface',
  'accessibility-services',
  'other-services',
  'pricing',
  'service-hours',
  'disruptions',
  'payment-interface'
  'other'
);

ALTER TABLE "external-interface-description"
-- Possible data content of the external interface
  ADD COLUMN "data-content" interface_data_content[];


-- Move data from external-interface-description/license_url to external-interface-description/external-interface/description
-- This will update the FI language of external interface description by concatenating data from license_url column into it
--   description in other languages will not be updated, as license_url contains text probably only in finnish.
--   FI language is the first element of the description array.
UPDATE "external-interface-description" eid
SET "external-interface".description[1].text = concat((eid."external-interface").description[1].text,
                                                       CASE
                                                       WHEN length((eid."external-interface").description[1].text) > 0
                                                            AND length(eid."license-url") > 0
                                                         THEN E'\nLisenssitiedot: '

                                                       ELSE ''
                                                       END,
                                                       eid."license-url");
