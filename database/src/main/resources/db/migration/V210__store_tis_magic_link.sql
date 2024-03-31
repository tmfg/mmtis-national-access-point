-- magic links are shareable URLs to specific results which don't need authentication on TIS side
ALTER TABLE "gtfs_package"
    ADD COLUMN "tis-magic-link" TEXT DEFAULT NULL;