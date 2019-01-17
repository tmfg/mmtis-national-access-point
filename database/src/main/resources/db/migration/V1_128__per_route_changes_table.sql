create table "gtfs-route-change" (
 "transit-change-date"        date not null,
 "transit-service-id"         integer not null references "transport-service" (id),
 "route-short-name"           text, 
 "route-long-name"            text not null, 
 "trip-headsign"              text, 
 "change-type"                "gtfs-route-change-type", 
 "added-trips"                integer default 0, 
 "removed-trips"              integer default 0, 
 "trip-stop-sequence-changes" int4range,
 "trip-stop-time-changes"     int4range,
 "current-week-date"          date, 
 "different-week-date"        date, 
 "change-date"                date,
 "created-date"               date,
 "route-hash-id"              text not null,
 foreign key ("transit-change-date", "transit-service-id") references "gtfs-transit-changes" ("date", "transport-service-id"));

do $$
 declare r record;
 c "gtfs-route-change-info";
begin
 for r in select date as d, "transport-service-id" as tsid, "route-changes" as rc from "gtfs-transit-changes" -- limit 10 -- for testing
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
	      null,
	      c."route-hash-id");

       -- raise notice 'd % c hs %', r.d, c."trip-headsign";
     end loop;
  end loop;
end
$$ language plpgsql;
