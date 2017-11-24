-- Split a11y to guaranteed and limited availability,
-- add info service accessibility

ALTER TYPE passenger_transportation_info
RENAME ATTRIBUTE "accessibility-tool" TO "guaranteed-accessibility-tool";

ALTER TYPE passenger_transportation_info
RENAME ATTRIBUTE "accessibility-description" TO "guaranteed-accessibility-description";

ALTER TYPE passenger_transportation_info
  ADD ATTRIBUTE "limited-accessibility-tool" accessibility_tool[],
  ADD ATTRIBUTE "limited-accessibility-description" localized_text[],
  ADD ATTRIBUTE "accessibility-info-url" VARCHAR(1024),
  ADD ATTRIBUTE "guaranteed-info-service-accessibility" accessibility_info_facility[],
  ADD ATTRIBUTE "limited-info-service-accessibility" accessibility_info_facility[];


-- Terminal services a11y


CREATE TYPE assistance_notification_requirement AS (
  "hours-before" INTEGER,
  telephone VARCHAR(16),
  email VARCHAR(200),
  url VARCHAR(1024)
);

CREATE TYPE assistance_info AS (
  "notification-requirements" assistance_notification_requirement,
  "description" localized_text[]
);

ALTER TYPE terminal_information
  ADD ATTRIBUTE assistance assistance_info ;


-- Add transportable mobility aid

CREATE TYPE transportable_aid AS ENUM (
 'wheelchair','walking-stick','assistance-dog','crutches',
 'walker' -- rollator (not from Texas)
);

ALTER TYPE rental_provider_informaton RENAME TO rental_provider_information;

ALTER TYPE rental_provider_information
  ADD ATTRIBUTE "guaranteed-transportable-aid" transportable_aid[],
  ADD ATTRIBUTE "limited-transportable-aid" transportable_aid[],
  ADD ATTRIBUTE "accessibility-info-url" VARCHAR(1024),
  ADD ATTRIBUTE "accessibility-description" localized_text[];

ALTER TYPE passenger_transportation_info
  ADD ATTRIBUTE "guaranteed-transportable-aid" transportable_aid[],
  ADD ATTRIBUTE "limited-transportable-aid" transportable_aid[];
