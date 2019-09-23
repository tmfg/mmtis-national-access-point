ALTER TYPE address
    ADD ATTRIBUTE "foreign_postal_code" VARCHAR(10);


CREATE OR REPLACE FUNCTION convert_transport_service_address_postalcodes()
RETURNS VOID AS $$
BEGIN
    -- Update service address
    UPDATE "transport-service" as ts
       SET ("contact-address"."foreign_postal_code") =
            (SELECT (service."contact-address")."postal_code" as postalcode
               FROM "transport-service" service
              WHERE service.id = ts.id);

    -- update rental pick up addressess
    UPDATE "transport-service" "t-service"
       SET (rentals."pick-up-locations") =
            (SELECT array_agg(locs.*)
               FROM "transport-service" service,
                    LATERAL UNNEST((service.rentals)."pick-up-locations") locs
              WHERE service.id = "t-service".id);
END;
$$ LANGUAGE plpgsql;

SELECT convert_transport_service_address_postalcodes();

CREATE OR REPLACE FUNCTION convert_transport_operator_address_postalcodes()
RETURNS VOID
AS $$
BEGIN
    UPDATE "transport-operator" as "t-operator"
       SET ("visiting-address"."foreign_postal_code") =
           (SELECT (top."visiting-address")."postal_code" as postalcode
             FROM "transport-operator" top
            WHERE top.id = "t-operator".id);

    UPDATE "transport-operator" as "t-operator"
       SET ("billing-address"."foreign_postal_code") =
           (SELECT (top."billing-address")."postal_code" as postalcode
              FROM "transport-operator" top
            WHERE top.id = "t-operator".id);

END;
$$ LANGUAGE plpgsql;

SELECT convert_transport_operator_address_postalcodes();

ALTER TYPE address
    DROP ATTRIBUTE "postal_code";

ALTER TYPE address
    RENAME ATTRIBUTE "foreign_postal_code" TO "postal_code";