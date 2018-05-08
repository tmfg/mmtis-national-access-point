-- Store file's ETag header to avoid duplicate file fetching.
ALTER TABLE gtfs_package
	ADD COLUMN etag text;
