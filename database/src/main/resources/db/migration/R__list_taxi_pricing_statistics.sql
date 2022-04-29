-- Functions for listing taxi service pricing statistics.
-- list_taxi_statistics(...) is meant to be called directly, other functions are helpers


-- functions need to be dropped first in case its return type changes, simple replace won't work in such case
-- also note various signatures; when adding new overloads, old ones need to be removed as well
DROP FUNCTION IF EXISTS list_taxi_pricing_statistics(TEXT, BOOLEAN);
DROP FUNCTION IF EXISTS list_taxi_pricing_statistics(TEXT, BOOLEAN, BOOLEAN);
DROP FUNCTION IF EXISTS list_taxi_statistics(TEXT, BOOLEAN, TEXT, BOOLEAN);
DROP FUNCTION IF EXISTS list_taxi_statistics(TEXT, BOOLEAN, TEXT, BOOLEAN, BOOLEAN);

-- Function wrapping is used to avoid a massive, repeating
-- ORDER BY clause; the quote_ident etc. keeps sure this function is safe from SQLi.
--
-- Original from https://stackoverflow.com/a/8146245/44523, modified to our needs
CREATE OR REPLACE FUNCTION list_taxi_pricing_statistics(
    ordering_column TEXT,
    ordering_direction BOOLEAN,
    approved BOOLEAN)
    RETURNS SETOF taxi_service_prices AS
$func$
BEGIN
    RETURN QUERY EXECUTE '
         SELECT *
           FROM (SELECT DISTINCT ON (service_id) *
                   FROM taxi_service_prices
                  WHERE "approved?" IS ' || (CASE WHEN approved = TRUE THEN 'NOT NULL ' ELSE ' NULL' END) || '
                  ORDER BY service_id,
                           timestamp DESC) AS s
                         ' || (CASE WHEN ordering_column IS NOT NULL AND ordering_direction IS NOT NULL
                     THEN 'ORDER BY s.' || QUOTE_IDENT(ordering_column) || ' ' ||
                          (CASE WHEN ordering_direction = TRUE
                                THEN 'ASC'
                                ELSE 'DESC'
                           END)
                     ELSE ''
                END);
END
$func$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION list_taxi_statistics(
    primary_ordering_column TEXT,
    primary_ordering_direction BOOLEAN,
    secondary_ordering_column TEXT,
    secondary_ordering_direction BOOLEAN,
    approved BOOLEAN)
    RETURNS TABLE (
                      "id" integer,
                      "operator-id" integer,
                      "service-id" integer,
                      "name" text,
                      "timestamp" timestamp,
                      "start-price-daytime" numeric,
                      "start-price-nighttime" numeric,
                      "start-price-weekend" numeric,
                      "price-per-minute" numeric,
                      "price-per-kilometer" numeric,
                      "accessibility_service_stairs" numeric,
                      "accessibility_service_stretchers" numeric,
                      "accessibility_service_fare" numeric,
                      "approved?" timestamp,
                      "approved-by" text,
                      "operating-areas" text[],
                      "example-trip" numeric
                  ) AS
$func$
DECLARE
    inner_ordering_column TEXT;
    inner_ordering_direction TEXT;
BEGIN
    inner_ordering_column := (CASE
                                  WHEN primary_ordering_column IS NULL
                                      THEN 'NULL'
                                  ELSE '''' || primary_ordering_column || ''''
                             END);
    inner_ordering_direction := (CASE
                                     WHEN primary_ordering_direction IS NULL
                                         THEN 'NULL'
                                     ELSE '''' || primary_ordering_direction || ''''
                                END);

    RETURN QUERY EXECUTE '
        SELECT l.id AS id,
               o.id AS "operator-id",
               s.id AS "service-id",
               CONCAT(o."name", ''/'', s."name") AS "name",
               l.timestamp AS "timestamp",
               l.start_price_daytime AS "start-price-daytime",
               l.start_price_nighttime AS "start-price-nighttime",
               l.start_price_weekend AS "start-price-weekend",
               l.price_per_minute AS "price-per-minute",
               l.price_per_kilometer AS "price-per-kilometer",
               l."accessibility_service_stairs" AS "accessibility-service-stairs",
               l."accessibility_service_stretchers" AS "accessibility-service-stretchers",
               l."accessibility_service_fare" AS "accessibility-service-fare",
               l."approved?" AS "approved?",
               l."approved-by" AS "approved-by",
               (SELECT array_agg(oa_d.text)
                  FROM operation_area oa,
                       unnest(description) AS oa_d
                 WHERE oa."transport-service-id" = l."service_id"
                   AND "primary?" = TRUE) AS "operating-areas",
               (l.start_price_daytime + (l.price_per_minute * 15) + (l.price_per_kilometer * 10)) AS "example-trip"
          FROM list_taxi_pricing_statistics(' || inner_ordering_column || ', ' || inner_ordering_direction || ', ' || approved || ') l
          JOIN "transport-service" s ON l."service_id" = s."id"
          JOIN "transport-operator" o ON s."transport-operator-id" = o."id"
         ' || (CASE WHEN secondary_ordering_column IS NOT NULL AND secondary_ordering_direction IS NOT NULL
                    THEN ' ORDER BY ' || QUOTE_IDENT(secondary_ordering_column) || ' ' ||
                         (CASE WHEN secondary_ordering_direction = TRUE
                               THEN 'ASC'
                               ELSE 'DESC'
                          END)
                    ELSE ''
               END);
END
$func$
LANGUAGE plpgsql;