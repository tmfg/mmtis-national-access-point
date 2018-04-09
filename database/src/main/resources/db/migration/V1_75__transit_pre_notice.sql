CREATE TYPE pre_notice_type AS ENUM (
  'termination',
  'new',
  'schedule-change',
  'route-change',
  'other'
);

CREATE TYPE notice_effective_date AS (
 "effective-date" DATE,
 "effective-date-description" TEXT
);

CREATE SEQUENCE pre_notice_attachment_number;

CREATE TYPE pre_notice_attachment AS (
  "attachment-file-name" TEXT,
  "attachment-number" INTEGER -- represents S3 file
);

CREATE TABLE pre_notice (
  id SERIAL PRIMARY KEY,
  "transport-operator-id" INTEGER REFERENCES "transport-operator" (id) NOT NULL,

  -- creation/modification metadata
  created timestamp with time zone DEFAULT NOW(),
  modified timestamp with time zone,
  "created-by" TEXT REFERENCES "user" (id),
  "modified-by" TEXT REFERENCES "user" (id),

  -- Actual content
  "pre-notice-type" pre_notice_type,
  "other-type-description" TEXT,

  "effective-dates" notice_effective_date[],

  "route-description" TEXT, -- short description of the routes that change
  regions CHARACTER(2)[], -- references finnish regions by region number

  -- URL and attachments
  url VARCHAR(1024),
  attachments pre_notice_attachment[]

);

COMMENT ON TABLE pre_notice IS
E'Notice about changes in transit routes, must be given to authorities 60 days in advance of
starting, stopping or changing routes.';
