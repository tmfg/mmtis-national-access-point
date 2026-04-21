-- DPO-3689: Remove Meri RAE (route editor) tables and types.
-- Meri RAE was disabled from the UI in spring 2025, this migration removes the
-- remaining database structures: transit_route(_trip/_stop_time), finnish_ports
-- and the associated composite/enum types.

DROP TABLE transit_route_stop_time;
DROP TABLE transit_route_trip;
DROP TABLE transit_route;
DROP TABLE finnish_ports;

DROP TYPE transit_service_calendar;
DROP TYPE transit_service_rule;
DROP TYPE transit_stop;
DROP TYPE transit_stop_type;
DROP TYPE transit_route_type;
DROP TYPE transit_stopping_type;
DROP TYPE transit_agency;

DROP SEQUENCE ote_user_stop_code;
