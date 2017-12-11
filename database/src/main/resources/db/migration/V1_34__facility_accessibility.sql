-- Merge mobility_facility and accessibility_info_facility enums into accessibility_facility

-- Add facility related values from MOBILITY_FACILITY enum

-- This might be useless, because we can use the 'lift', 'ramp' etc. values to define if the facility has step free access.
ALTER TYPE accessibility_facility ADD VALUE 'step-free-access';
ALTER TYPE accessibility_facility ADD VALUE 'suitable-for-wheelchairs';-- Added as per specification
-- NOTE: Fixes a typo that was in the original enum (tactile-patform-edges)
ALTER TYPE accessibility_facility ADD VALUE 'tactile-platform-edges';
ALTER TYPE accessibility_facility ADD VALUE 'tactile-guiding-strips';

  -- Add values from ACCESSIBILITY_INFO_FACILITY
ALTER TYPE accessibility_facility ADD VALUE 'audio-for-hearing-impaired';
ALTER TYPE accessibility_facility ADD VALUE 'audio-information';
ALTER TYPE accessibility_facility ADD VALUE 'visual-displays';
ALTER TYPE accessibility_facility ADD VALUE 'displays-for-visually-impaired';
ALTER TYPE accessibility_facility ADD VALUE 'large-print-timetables';
