-- napote=# \d "gtfs-route-change-info"
--           Composite type "public.gtfs-route-change-info"
--            Column           |           Type           | Modifiers 
-- ----------------------------+--------------------------+-----------
--  route-short-name           | text                     | 
--  route-long-name            | text                     | 
--  trip-headsign              | text                     | 
--  change-type                | "gtfs-route-change-type" | 
--  added-trips                | integer                  | 
--  removed-trips              | integer                  | 
--  trip-stop-sequence-changes | int4range                | 
--  trip-stop-time-changes     | int4range                | 
--  current-week-date          | date                     | 
--  different-week-date        | date                     | 
--  change-date                | date                     | 
--  route-hash-id              | text                     | 

create table "gtfs-route-change" (
 "transit-change-date"        date not null,
 "transit-service-id"         integer not null references "transport-service" (id),
 "route-short-name"           text not null, 
 "route-long-name"            text not null, 
 "trip-headsign"              text not null, 
 "change-type"                "gtfs-route-change-type", 
 "added-trips"                integer not null, 
 "removed-trips"              integer not null, 
 "trip-stop-sequence-changes" int4range not null,
 "trip-stop-time-changes"     int4range not null,
 "current-week-date"          date not null, 
 "different-week-date"        date not null, 
 "change-date"                date not null, 
 "route-hash-id"              text not null,
 foreign key ("transit-change-date", "transit-service-id") references "gtfs-transit-changes" ("date", "transport-service-id"));

do $$
 declare r record;
 c "gtfs-route-change-info";
begin
 for r in select date as d "transport-service-id" as tsid, "route-changes" as rc from "gtfs-transit-changes" limit 10
 loop
   foreach c in array rc."route-changes" -- todo: find working syntax to do this
     loop
       -- todo: insert statement here
       raise notice 'd % c hs %', date, c."trip-headsign";
     end loop;
  end loop;
end
$$;

-- todo: drop array column after data is copied to new table


