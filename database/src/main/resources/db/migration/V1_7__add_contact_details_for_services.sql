-- Remove contact details from passenger_transportation_info
ALTER TYPE "passenger_transportation_info"
    DROP ATTRIBUTE  "contact-address",
    DROP ATTRIBUTE  "contact-phone",
    DROP ATTRIBUTE  "contact-gsm",
    DROP ATTRIBUTE  "contact-email",
    DROP ATTRIBUTE  homepage;


-- Add contact details to transport-service
ALTER TABLE "transport-service"
    ADD COLUMN "contact-address" address,
    ADD COLUMN "contact-phone" VARCHAR(16),
    ADD COLUMN "contact-gsm" VARCHAR(16),
    ADD COLUMN "contact-email" VARCHAR(200),
    ADD COLUMN homepage VARCHAR(1024);

