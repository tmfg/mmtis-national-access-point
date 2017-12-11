-- Merge mobility_facility enum into accessibility_facility

-- This might be useless, because we can use the 'lift', 'ramp' etc. values to define if the facility has step free access.
ALTER TYPE accessibility_facility ADD VALUE 'step-free-access';
ALTER TYPE accessibility_facility ADD VALUE 'suitable-for-wheelchairs';-- Added as per specification
-- NOTE: Fixes a typo that was in the original enum (tactile-patform-edges)
ALTER TYPE accessibility_facility ADD VALUE 'tactile-platform-edges';
ALTER TYPE accessibility_facility ADD VALUE 'tactile-guiding-strips';
