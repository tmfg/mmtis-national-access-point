-- Add a description column in pre-notice table to store generic info about the changes in a pre-notice
ALTER TABLE pre_notice
  ADD COLUMN "description" TEXT;
