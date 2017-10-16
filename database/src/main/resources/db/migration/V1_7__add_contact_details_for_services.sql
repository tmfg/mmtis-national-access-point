-- Add contact details to terminal_information
ALTER TYPE "terminal_information"
    ADD ATTRIBUTE "contact-address" address,
    ADD ATTRIBUTE "contact-phone" VARCHAR(16),
    ADD ATTRIBUTE "contact-gsm" VARCHAR(16),
    ADD ATTRIBUTE "contact-email" VARCHAR(200),
    ADD ATTRIBUTE homepage VARCHAR(1024);


-- Add contact details to rental_provider_informaton
ALTER TYPE "rental_provider_informaton"
    ADD ATTRIBUTE "contact-address" address,
    ADD ATTRIBUTE "contact-phone" VARCHAR(16),
    ADD ATTRIBUTE "contact-gsm" VARCHAR(16),
    ADD ATTRIBUTE "contact-email" VARCHAR(200),
    ADD ATTRIBUTE homepage VARCHAR(1024);


-- Add contact details to parking_provider_information
ALTER TYPE "parking_provider_information"
    ADD ATTRIBUTE "contact-address" address,
    ADD ATTRIBUTE "contact-phone" VARCHAR(16),
    ADD ATTRIBUTE "contact-gsm" VARCHAR(16),
    ADD ATTRIBUTE "contact-email" VARCHAR(200),
    ADD ATTRIBUTE homepage VARCHAR(1024);

-- Add contact details to brokerage_provider_informaton
ALTER TYPE "brokerage_provider_informaton"
    ADD ATTRIBUTE "contact-address" address,
    ADD ATTRIBUTE "contact-phone" VARCHAR(16),
    ADD ATTRIBUTE "contact-gsm" VARCHAR(16),
    ADD ATTRIBUTE "contact-email" VARCHAR(200),
    ADD ATTRIBUTE homepage VARCHAR(1024);


-- Remove location from terminal_information
ALTER TYPE "terminal_information"
    DROP ATTRIBUTE location;
