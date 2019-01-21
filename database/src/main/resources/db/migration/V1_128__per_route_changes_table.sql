CREATE TABLE "gtfs-route-change" (
 "transit-change-date"        date not null,
 "transit-service-id"         integer not null references "transport-service" (id),
 "route-short-name"           text,                   -- "15"
 "route-long-name"            text not null,          -- "Helsinki-Oulu"
 "trip-headsign"              text, 
 "change-type"                "gtfs-route-change-type", 
 "added-trips"                integer default 0, 
 "removed-trips"              integer default 0, 
 "trip-stop-sequence-changes" int4range,              -- Range of change counts in trips (0 - 2)
 "trip-stop-time-changes"     int4range,              -- Range of stop time changes in trips (0 - 200)
 "current-week-date"          date,                   -- Day of detection run
 "different-week-date"        date,                   -- The date when the change happens. Exception when we are in the middle no-traffic, date is the first day of traffic
 "change-date"                date,                   -- Date for next the detection run
 "route-hash-id"              text not null,          -- routes key between gtfs packages ("route-short-name" - "route-long-name" - "trip-headsign")
 "created-date"               date,
 foreign key ("transit-change-date", "transit-service-id") references "gtfs-transit-changes" ("date", "transport-service-id"));

do $$
 declare r record;
 c "gtfs-route-change-info";
begin
 for r in select date as d, "transport-service-id" as tsid, "route-changes" as rc from "gtfs-transit-changes"
 loop
   foreach c in array r.rc
     loop
       insert into "gtfs-route-change" values (r.d, r.tsid,
       	      c."route-short-name",
       	      c."route-long-name",
	      c."trip-headsign",
	      c."change-type",
	      c."added-trips",
	      c."removed-trips",
	      c."trip-stop-sequence-changes",
	      c."trip-stop-time-changes",
	      c."current-week-date",
	      c."different-week-date",
      	      c."change-date",
	      COALESCE(c."route-hash-id",
                 c."route-short-name" || '-' || c."route-long-name" || '-' || c."trip-headsign"),
	      null);

       -- raise notice 'd % c hs %', r.d, c."trip-headsign";
     end loop;
  end loop;
end
$$ language plpgsql;


-- TODO: ALTER TABLE "gtfs-transit-changes"
--         DROP COLUMN "route-changes";

-- TODO: Add an index to this table to date and service-id

-- TODO: Change name of the table to "detected-route-change"