-- Add semi-optional column "feature_id" to operation area to support GTFS Flex locations.geojson schema requirements
-- In code the feature_id should default to first whatever localized text is available if feature_id is null. There's no
-- data migration here for that on purpose, since manually modifying feature ids is an upcoming GUI feature.
ALTER TABLE "operation_area"
    ADD COLUMN feature_id TEXT;