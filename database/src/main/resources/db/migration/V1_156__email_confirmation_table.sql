CREATE TABLE "email-confirmation-token" (
    "user-email" text UNIQUE,
    token text PRIMARY KEY,
    expiration date NOT NULL
)
