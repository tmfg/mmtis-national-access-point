ALTER TYPE address
    ADD ATTRIBUTE "country-code" CHAR(2);


CREATE OR REPLACE FUNCTION convert_transport_service_address_countrycodes()
    RETURNS VOID AS $$
BEGIN
    -- Update service address
    UPDATE "transport-service" as ts
    SET ("contact-address"."country-code") = 'FI';

    -- update rental pick up addresses
    UPDATE "transport-service" as ts
    SET (rentals."pick-up-locations") =
            (SELECT array_agg(ROW(pa."pick-up-name",
                pa."pick-up-type",
                pa."service-hours",
                pa."service-exceptions",
                ((pa."pick-up-address").street,
                 (pa."pick-up-address").postal_code,
                 (pa."pick-up-address").post_office,
                 (pa."pick-up-address").foreign_postal_code,
                 'FI'
                 )::address,
                pa."service-hours-info")::pick_up_location)
             FROM "transport-service" service
                      join lateral unnest((service.rentals)."pick-up-locations") pa on true
    WHERE service.type = 'rentals' AND ts.id = service.id
                         GROUP BY service.id);
END;
$$ LANGUAGE plpgsql;

SELECT convert_transport_service_address_countrycodes();

CREATE OR REPLACE FUNCTION convert_transport_operator_address_countrycodes()
    RETURNS VOID
AS $$
BEGIN
    UPDATE "transport-operator" as "t-operator"
    SET ("visiting-address"."country-code") = 'FI';

    UPDATE "transport-operator" as "t-operator"
    SET ("billing-address"."country-code") = 'FI';

END;
$$ LANGUAGE plpgsql;

SELECT convert_transport_operator_address_countrycodes();
