ALTER TABLE "user"
ADD COLUMN "email-confirmed?" BOOLEAN DEFAULT FALSE,
ADD COLUMN "confirmation-time" TIMESTAMP;
