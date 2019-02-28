-- Create table hash-recalculation which stores data related hash calculations. When calculation is running
-- it should not be started for different service or other set of packages. It is too heavy operation. So
-- this table is working as a kind of a lock for that.

CREATE TABLE "hash-recalculation"
(
  id SERIAL PRIMARY KEY,
  started TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  "packets-ready" INTEGER,
  "packets-total" INTEGER,
  completed TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  "created-by" TEXT REFERENCES "user" (id)
);