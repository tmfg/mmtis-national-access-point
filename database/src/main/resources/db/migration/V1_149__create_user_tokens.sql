CREATE TABLE "user-tokens" (
    "user-email" text NOT NULL UNIQUE,
    token text NOT NULL PRIMARY KEY,
    "operator-id" INTEGER NOT NULL,
    expiration date NOT NULL,
    "requester-id" text NOT NULL
)
