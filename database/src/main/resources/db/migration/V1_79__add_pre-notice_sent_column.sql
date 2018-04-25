-- Add pre-notice state "sent" column that stores the DateTime when the pre-notice was sent. --

ALTER TABLE pre_notice
  ADD COLUMN "sent" TIMESTAMP WITH TIME ZONE;