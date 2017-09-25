Taxi geojson example json file contains a taxi operator and three transport services:
- Taxi service normal operating area
- Taxi station
- Taxi stand

Both the service operator and the transportation services are GeoJSON Features with their
own geometry.

Transportation services refer to the operator who runs them with the "transport-operator-id"
property which must be unique within the document. It is suggested to use globally unique
identifiers, when available, e.g. business-id.

Different service types have different required fields.

All textual descriptions meant for human consumption are presented as an array of
maps where each element contains a 2-character language code (ISO 639-1) and the text in
that language.