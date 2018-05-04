-- Records information about a downloaded GTFS package
CREATE TABLE gtfs_package (
 id SERIAL PRIMARY KEY,
 "transport-operator-id" INTEGER REFERENCES "transport-operator" (id),
 "transport-service-id" INTEGER REFERENCES "transport-service" (id),
 created TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
 "sha256" VARCHAR(64),
 "routes-hash" TEXT, -- SHA256(comma separated sorted list of route short names for comparison)
 "stops-hash" TEXT, -- SHA256(comma separated sorted list of stop codes for comparison)
 "trips-hash" TEXT -- SHA256(list of all trips stop times and exceptions)
);

ALTER TABLE "external-interface-description"
	ADD COLUMN "gtfs-imported" timestamp with time zone;
