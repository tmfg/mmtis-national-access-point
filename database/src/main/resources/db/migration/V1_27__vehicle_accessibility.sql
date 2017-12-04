-- Enumeration describing the accessibility properties of a VEHICLE, does not contain
-- accessibility services like assistance, only the physical properties of the vehicle.
CREATE TYPE vehicle_accessibility AS ENUM (
  'low-floor', 'step-free-access', 'accessible-vehicle', 'suitable-for-wheelchairs',
  'boarding-assistance', 'assistance-dog-space'
);

ALTER TYPE passenger_transportation_info
  ADD ATTRIBUTE "guaranteed-vehicle-accessibility" vehicle_accessibility[],
  ADD ATTRIBUTE "limited-vehicle-accessibility" vehicle_accessibility[];
