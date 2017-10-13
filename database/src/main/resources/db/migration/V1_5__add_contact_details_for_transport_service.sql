-- Add contact details to passenger_transportation_info
ALTER TYPE "passenger_transportation_info"
    ADD ATTRIBUTE "contact-address" address,
    ADD ATTRIBUTE "contact-phone" VARCHAR(16),
    ADD ATTRIBUTE "contact-gsm" VARCHAR(16),
    ADD ATTRIBUTE "contact-email" VARCHAR(200),
    ADD ATTRIBUTE homepage VARCHAR(1024);