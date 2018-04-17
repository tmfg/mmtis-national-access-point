-- Change pre-notice-type to array
ALTER TABLE pre_notice
   DROP COLUMN "pre-notice-type",
   ADD COLUMN "pre-notice-type" pre_notice_type[];