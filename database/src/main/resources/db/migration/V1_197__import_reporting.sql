-- Tables and structures for storing import related error reporting content
CREATE TABLE gtfs_import_report
(
    id          SERIAL PRIMARY KEY,
    package_id  INT                         NOT NULL,
    severity    TEXT                        NOT NULL,
    description TEXT                        NOT NULL,
    error       BYTEA                       NOT NULL,
    FOREIGN KEY (package_id) REFERENCES "gtfs_package" (id)
);
