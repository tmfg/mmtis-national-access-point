-- Password reset request
CREATE TABLE "password-reset-request" (
  "request-id" SERIAL PRIMARY KEY,
  created TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  "created-by" TEXT REFERENCES "user" (id),
  "reset-key" UUID,
  used TIMESTAMP WITH TIME ZONE
);
