ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_email_unique UNIQUE (email),
    ALTER COLUMN "email" SET NOT NULL;
