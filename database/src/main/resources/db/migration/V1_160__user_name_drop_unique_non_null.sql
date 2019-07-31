ALTER TABLE "user"
    DROP CONSTRAINT "user_name_key",
    ALTER COLUMN "name" DROP NOT NULL;
