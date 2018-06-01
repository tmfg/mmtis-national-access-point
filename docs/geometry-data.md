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

## Countries


### Finland
Geometry for Finland was generated with QGIS by combining all counties and dissolving them with the
vector tool. The resulting area is taken with buffer (0.05 tolerance).

### Other countries

- Source: Natural Earth Data
http://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/cultural/ne_10m_admin_0_countries.zip
- License: Free to use public domain (http://www.naturalearthdata.com/about/terms-of-use/)
- Generated GeoJSON from that data containing country code, spatial data and english names using script https://github.com/datasets/geo-countries/tree/master/scripts.
- GeoJSON fields have been modified to match "country" table columns
- GeoJSON inserted into country table with command:

> ogr2ogr -f "PostgreSQL" PG:"dbname=napote user=napote host=localhost" ne_10m_admin_0_cyprus.geojson -nln country  -t_srs "+proj=latlong +datum=WGS84 +axis=enu +wktaext"

- GeoJSON original country codes did not match ISO3166-1-standard and duplicates have been replaced
- GeoJSON didn't have finnish or swedish country names, those have been added with a script
- This was converted into a migration SQL file

## Continents

- Continent data from https://gist.github.com/hrbrmstr/91ea5cc9474286c72838
- License: MIT Licensed
- This was converted into a migration SQL file
