-- approval related modifications to DDL
-- each approval tracks who approved it and when; this is mainly to enable auditing in case something goes surprisingly
-- wrong
ALTER TABLE taxi_service_prices
    ADD COLUMN "approved?"   TIMESTAMP,
    ADD COLUMN "approved-by" TEXT,
    ADD FOREIGN KEY ("approved-by") REFERENCES "user" (id);