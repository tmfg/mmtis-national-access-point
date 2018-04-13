-- Add pre-notice state type --
CREATE TYPE pre_notice_state AS ENUM (
   'draft',
   'sent',
   'read'
);

ALTER TABLE pre_notice
  ADD COLUMN "pre-notice-state" pre_notice_state DEFAULT 'draft';