## Geometry data needs to be inserted into the database for place search to work

Use ogr2ogr (in GDAL framework) to convert from ESRI Shapefile to PostGIS.
The tool will insert the data.

Example command:

`/Library/Frameworks/GDAL.framework/Versions/2.1/Programs/ogr2ogr -f "PostgreSQL" PG:"dbname=napote user=napote host=localhost" ~/suomenkunnat.shp -nln finnish_municipalities -s_srs EPSG:3067 -t_srs "+proj=latlong +datum=WGS84 +axis=neu +wktext"`


Options:
 - `-f "PostgreSQL"` set the output format to postgres insert
 - `PG:"database connection"` defines the pg connection to use
 - `-nln tablename` sets the target table name
 - `-s_srs` source SRS of the Shapefile (like EPSG:3067 for Finnish materials)
 - `-t_srs` target SRS (WGS84, the `+axis=neu` flips x/y)
