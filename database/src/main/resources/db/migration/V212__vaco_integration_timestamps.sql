ALTER TABLE "gtfs_package"
    DROP COLUMN "tis-error",
    ADD COLUMN tis_submit_error TEXT, -- Polling error might happen
    ADD COLUMN tis_polling_error TEXT, -- Polling error might happen
    ADD COLUMN tis_submit_started TIMESTAMP WITH TIME ZONE, -- Feed submission started
    ADD COLUMN tis_submit_completed TIMESTAMP WITH TIME ZONE, -- Feed submission ended
    ADD COLUMN tis_polling_started TIMESTAMP WITH TIME ZONE, -- Feed polling started
    ADD COLUMN tis_polling_completed TIMESTAMP WITH TIME ZONE; -- Feed polling completed