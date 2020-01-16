-- Enable acceptance of terms of service for user
ALTER TABLE "user"
    ADD COLUMN "accepted-tos?" boolean DEFAULT FALSE,
    ADD COLUMN "seen-tos?" boolean DEFAULT FALSE;

