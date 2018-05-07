-- Records information about a downloaded GTFS package
CREATE TABLE gtfs_package (
 id SERIAL PRIMARY KEY,
 "transport-operator-id" INTEGER REFERENCES "transport-operator" (id) ON DELETE CASCADE,
 "transport-service-id" INTEGER REFERENCES "transport-service" (id) ON DELETE CASCADE,
 created TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
 "sha256" VARCHAR(64),
 "routes-hash" TEXT, -- SHA256(comma separated sorted list of route short names for comparison)
 "stops-hash" TEXT, -- SHA256(comma separated sorted list of stop codes for comparison)
 "trips-hash" TEXT -- SHA256(list of all trips stop times and exceptions)
);

ALTER TABLE "external-interface-description"
	ADD COLUMN "gtfs-imported" timestamp with time zone;

-- GTFS package as database tables

CREATE TABLE "gtfs-agency"
(
  id                  SERIAL PRIMARY KEY,
  "package-id"		  INTEGER NOT NULL REFERENCES gtfs_package (id),
  "agency-id"         text,
  "agency-name"       text NOT NULL,
  "agency-url"        text NOT NULL,
  "agency-fare-url"   text,
  "agency-timezone"   text NOT NULL,
  "agency-lang"       text,
  "agency-phone"      text,
  "agency-email"	  text
);

CREATE TABLE "gtfs-stop"
(
  id                  SERIAL PRIMARY KEY,
  "package-id"		  INTEGER NOT NULL REFERENCES gtfs_package (id),
  "stop-id"           text NOT NULL,
  "stop-code"         text,
  "stop-name"         text NOT NULL,
  "stop-desc"         text,
  "stop-lat"          numeric NOT NULL,
  "stop-lon"          numeric NOT NULL,
  "zone-id"           text,
  "stop-url"          text,
  "location-type"     INTEGER CHECK ("location-type" BETWEEN 0 AND 2), -- 0,1,2
  "parent-station"    text,
  "stop-timezone"     text
);

CREATE TABLE "gtfs-route"
(
  id                  SERIAL PRIMARY KEY,
  "package-id"		  INTEGER NOT NULL REFERENCES gtfs_package (id),
  "route-id"          text,
  "agency-id"         text,
  "route-short-name"  text,
  "route-long-name"   text,
  "route-desc"        text,
  "route-type"        integer,
  "route-url"         text,
  "route-color"       text,
  "route-text-color"  text
);

CREATE TABLE "gtfs-calendar"
(
  id                  SERIAL PRIMARY KEY,
  "package-id"		  INTEGER NOT NULL REFERENCES gtfs_package (id),
  "service-id"        text,
  "monday"            boolean NOT NULL,
  "tuesday"           boolean NOT NULL,
  "wednesday"         boolean NOT NULL,
  "thursday"          boolean NOT NULL,
  "friday"            boolean NOT NULL,
  "saturday"          boolean NOT NULL,
  "sunday"            boolean NOT NULL,
  "start-date"        DATE NOT NULL,
  "end-date"          DATE NOT NULL
);

CREATE TABLE "gtfs-calendar-date"
(
  id                  SERIAL PRIMARY KEY,
  "package-id"		  INTEGER NOT NULL REFERENCES gtfs_package (id),
  "service-id" 		  text NOT NULL,
  "date" 			  DATE NOT NULL,
  "exception-type"    integer NOT NULL
);

CREATE TYPE route_shape AS
(
	"shape-pt-lat"      	NUMERIC ,
	"shape-pt-lon"      	NUMERIC ,
	"shape-pt-sequence" 	integer ,
	"shape-dist-traveled" 	NUMERIC
);

CREATE TABLE "gtfs-shape"
(
  id                  SERIAL PRIMARY KEY,
  "package-id"		  INTEGER NOT NULL REFERENCES gtfs_package (id),
  "shape-id"          text,
  "route-shape"               route_shape[]
);

CREATE TABLE "gtfs-trip"
(
  id                  SERIAL PRIMARY KEY,
  "package-id"		  INTEGER NOT NULL REFERENCES gtfs_package (id),
  "route-id"          text NOT NULL,
  "service-id"        text NOT NULL,
  "trip-id"           text NOT NULL,
  "trip-headsign"     text,
  "direction-id"      integer CHECK ("direction-id" IN (0,1)),
  "block-id"          text,
  "shape-id"          text,
  "wheelchair-accessible" integer NULL CHECK("wheelchair-accessible" BETWEEN 0 AND 2)
);

CREATE TABLE "gtfs-stop-time"
(
  id                  SERIAL PRIMARY KEY,
  "package-id"		  INTEGER NOT NULL REFERENCES gtfs_package (id),
  "trip-id"           text NOT NULL,
  "arrival-time"      interval NOT NULL,
  "departure-time"    interval NOT NULL,
  "stop-id"           text NOT NULL,
  "stop-sequence"     integer NOT NULL,
  "stop-headsign"     text,
  "pickup-type"       integer NULL CHECK("pickup-type" BETWEEN 0 AND 3),
  "drop-off-type"     integer NULL CHECK("drop-off-type" BETWEEN 0 AND 3),
  "shape-dist-traveled" numeric
);

CREATE TABLE "gtfs-transfer"
(
  id                  SERIAL PRIMARY KEY,
  "package-id"		  INTEGER NOT NULL REFERENCES gtfs_package (id),
  "from-stop-id"  	  text NOT NULL,
  "to-stop-id"        text NOT NULL,
  "transfer-type"     integer NOT NULL,
  "min-transfer-time" integer
);
