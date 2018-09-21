-- Password reset request
CREATE TABLE "password-reset-request" (
  "request-id" SERIAL PRIMARY KEY,
  "user-id" TEXT REFERENCES user (id),
  created TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  "reset-key" UUID,
  used TIMESTAMP WITH TIME ZONE
);
