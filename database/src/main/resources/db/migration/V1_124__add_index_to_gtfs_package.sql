-- Add index to speed up order by using e_id
CREATE INDEX "interface-id" ON gtfs_package ("external-interface-description-id");
