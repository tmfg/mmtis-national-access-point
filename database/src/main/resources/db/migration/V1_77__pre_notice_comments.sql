CREATE TABLE pre_notice_comment (
  id SERIAL PRIMARY KEY,
  "pre-notice-id" INTEGER REFERENCES pre_notice (id),
  "created-by" TEXT REFERENCES "user" (id),
  created TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  comment TEXT
);
