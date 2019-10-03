ALTER TYPE address
    ADD ATTRIBUTE "country_code" CHAR(2);


CREATE OR REPLACE FUNCTION convert_transport_service_address_countrycodes()
    RETURNS VOID AS $$
BEGIN
    -- Update service address
    UPDATE "transport-service" as ts
    SET ("contact-address"."country_code") =
            (SELECT 'FI'
             FROM "transport-service" service
             WHERE service.id = ts.id);

    -- update rental pick up addresses
    UPDATE "transport-service" as ts
    SET (rentals."pick-up-locations") =
            (SELECT array_agg(ROW(pa."pick-up-name",
                    pa."pick-up-type",
                    pa."service-hours",
                    pa."service-exceptions",
                    ((pa."pick-up-address").street,
                     (pa."pick-up-address").post_office,
                     (pa."pick-up-address").postal_code,
                     'FI'
                    )::address,
                    pa."service-hours-info")::pick_up_location)
               FROM "transport-service" service,
                    lateral unnest((service.rentals)."pick-up-locations") pa
              WHERE service.type = 'rentals' AND ts.id = service.id
              GROUP BY service.id);
END;
$$ LANGUAGE plpgsql;

SELECT convert_transport_service_address_countrycodes();
DROP FUNCTION convert_transport_service_address_countrycodes();

CREATE OR REPLACE FUNCTION convert_transport_operator_address_countrycodes()
    RETURNS VOID
AS $$
BEGIN
    UPDATE "transport-operator" as "t-operator"
    SET ("visiting-address"."country_code") =
            (SELECT 'FI'
             FROM "transport-operator" top
             WHERE top.id = "t-operator".id);

    UPDATE "transport-operator" as "t-operator"
    SET ("billing-address"."country_code") =
            (SELECT 'FI'
             FROM "transport-operator" top
             WHERE top.id = "t-operator".id);

END;
$$ LANGUAGE plpgsql;

SELECT convert_transport_operator_address_countrycodes();
DROP FUNCTION convert_transport_operator_address_countrycodes();
