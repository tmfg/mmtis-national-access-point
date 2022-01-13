-- Function for listing taxi service pricing statistics. Function wrapping is used to avoid massive, repeating
-- ORDER BY clause; the quote_ident etc. keeps sure this function is safe from SQLi.
--
-- Original from https://stackoverflow.com/a/8146245/44523, modified to our needs
CREATE OR REPLACE FUNCTION list_taxi_pricing_statistics(ordering_column TEXT, ordering_direction BOOLEAN)
    RETURNS SETOF taxi_service_prices AS
$func$
BEGIN
    RETURN QUERY EXECUTE '
         SELECT *
           FROM (SELECT DISTINCT ON (service_id) *
                   FROM taxi_service_prices
                  ORDER BY service_id,
                           timestamp DESC) AS s
          ORDER BY s.' || QUOTE_IDENT(ordering_column) || ' ' ||
                         (CASE WHEN ordering_direction = TRUE THEN 'ASC' ELSE 'DESC' END);
END
$func$
LANGUAGE plpgsql;