# FINAP service catalog API

FINAP backend exposes to 3rd party developers the same query interface as the application uses.
The interface can be used without authentication.

## Service search

The service search API is used for fetching information about published transport services.
Search returns basic information about the operator and the service that can be used to fetch
more information about the service.

Service search is available at `https://finap.fi/ote/service-search`.

Example call with [curl](https://curl.haxx.se/) tool to fetch all taxi services:

`$ curl "https://finap.fi/ote/service-search?sub_types=taxi&response_format=json"`


### Parameters

The query is a GET request and all the search filters are given as query parameters.

Name | Description | Example
---- | ----------- | -------
text | Free text search term (searches the name of the service) | `bus`
operation_area | Comma separated list, matches services that have any of the listed areas as their operation area | `Oulu,Kempele`
sub_types | Comma separated list, matches services of the listed type (see service types) | `terminal,taxi`
operators | List of operator ids (see operator search), matches any service created by the given operators | `4,8,15,16,23,42`
limit | Max number of results to return | `25`
offset | Where to start returning results (for paging) | `0`
response_format | Output format (defaults to transit) | `json`

All parameters are optional. If no parameters are specified the search will return the latest services.
The default output format is Transit but can be changed to JSON with the `response_format` query parameter.

### Service types

There are 7 service types (4 main types with 4 sub types for passenger transportation).
All the types are represented in the "sub_types" parameter list.

Valid values are

Value | Description
----- | -----------
taxi | Taxi service (road traffic)
request | Charter traffic and other equest based
schedule | Regular scheduled traffic 
terminal | Stations, ports and other terminals
rentals | Vehicle rentals and commercial shared vehicle solutions
parking | General commercial parking services


### Response values

The following shows an example of the response in the JSON format:

```json
{
  "empty-filters?": false,
  "total-service-count": 123,
  "results": [
    {
      "transport-operator-id": 1,
      "type": "terminal",
      "operator-name": "Some operator, Inc",
      "name": "The bus stop",
      "sub-type": "terminal",
      "id": 2,
      "published":"1970-01-01T00:00:00Z",
      "description":[
        {"lang":"FI","text":"Bussipysäkki keskellä suomea"}, 
        {"lang":"EN","text":"Bus stop in the middle of nowhere"}],
      "business-id":"1234567-0",
      "external-interface-links":[
      {"external-interface":
            {"url":"url",
             "description":[
               {"lang":"FI","text":"Matkahinnat, toimialue, aukioloajat, tilaustavat: url"},
               {"lang":"EN","text":"See more info from: url"}]},
             "format":["GTFS"],
             "license":"url",
             "data-content":["pricing"]}],
             "url-ote-netex": "http://finap.fi/export/netex/1/1",
      "homepage":"url"
    },
    ... more results ...
  ],
  "filter-service-count": 10
}
```

The response is an object containing some information about the search (like total and filtered service counts)
and a results array. The `results` key has an array of the returned services. Each service is an
object with the service's information like name, type, contact-address and operator.
More information may be added to the results in the future.


## GeoJSON export

All services also have their own GeoJSON export, which contains more information about the service.
The URL for the GeoJSON can be formed with the `transport-operator-id` and `id` fields in the
service-search API response `result` objects.

The format is:
> `https://finap.fi/ote/export/geojson/<transport-operator-id>/<id>`

Substitute `<transport-operator-id>` and `<id>` using values from the JSON result.

Please note that "external-interface" links obtained from exported NAP GeoJSON service descriptions do not always lead directly to the actual machine-readable data or API. Sometimes the link can lead to a human-readable web page containing information about how to access the data or API. When assuming that the external-interface link is a direct link to the external data or API, errors can occur in your application.

The Finnish Transport and Communications Agency assumes no responsibility for the correctness of the transport service information obtained by using the FINAP Service Catalog API. If you encounter errors other than technical problems with service search API or the format of responses, please contact the service provider in question.

### GeoJSON schema

The up to date [json-schema](http://json-schema.org/) of the GeoJSON is always available at `https://finap.fi/export/geojson/transport-service.schema.json`. The schema does not define the GeoJSON structures of the geometries, as those are in the GeoJSON specification. The schema contains the all the properties (and their values, like enums) of the different transport-service types. 


### Example GeoJSON output

The following is a simple output example output for a terminal service.
Different service types have different values in the properties.

All GeoJSON exports have a single feature, with a `geometry`.
The `properties` will contain keys `transport-operator` and `transport-service` for
information about the operator company and the service itself respectively.

Geometry may be any type of geographical area, including a specific point or even a whole country
depending on the service operation area.

Human readable text information is represented as an array of objects where each object contains
keys `lang` and `text`. The `lang` key is the ISO 639-1 two-letter language code (like "FI") and
the `text` is the text in that language. Multiple languages may be present in a service.

```json
{"type": "FeatureCollection",
 "features": [
   {"type": "Feature",
    "properties": {
       "transport-operator": {
         "business-id": "1234567-8",
         "name": "Some operator, Inc",
         "homepage": "http://www.example.com"
       },
       "transport-service": {
         "sub-type": "terminal",
         "ckan-resource-id": "b066e94b-48a2-4ace-b20b-052e70d7faff",
         "ckan-dataset-id": "f81df018-2a6b-47b1-acb3-a5c75461fbbd",
         "terminal": {
           "information-service-accessibility": [
             "visual-displays",
             "large-print-timetables"
           ],
           "accessibility-tool": [],
           "assistance": {
             "notification-requirements": {},
             "description": [],
             "assistance-place-description": []
           },
           "services": [],
           "accessibility-description": [],
           "accessibility": [
             "suitable-for-wheelchairs"
           ],
           "service-hours-info": [],
           "service-exceptions": [],
           "service-hours": [
             {
               "week-days": ["MON","TUE","WED","THU","FRI","SAT","SUN"],
               "from": {
                 "hours": 0,
                 "minutes": 0,
                 "seconds": 0
               },
               "to": {
                 "hours": 24,
                 "minutes": 0,
                 "seconds": 0
               },
               "description": [],
               "all-day": true
             }
           ],
           "indoor-map": {
             "description": []
           }
         },
         "description": [{"lang": "FI", "text": "This is a bus stop. Buses from here go somewhere."}],
         "brokerage?": false,
         "external-interfaces": null,
         "contact-address": {
           "street": "Somestreet 6",
           "postal_code": "12345",
           "post_office": "Someplace"
         },
         "name": "Bus stop to somewhere",
         "type": "terminal",
         "transport-operator-id": 1
       }
     },
     "geometry": {
       "type": "MultiPoint",
       "coordinates": [[25.654342, 65.070461]]
     }
   }
 ]
}
```


## Operator completions

Service search allows filtering based on the transport operator id.
The operator completions service can be used to fetch the ids of operators based on a name match.

Operator completions is available at: `https://finap.fi/ote/operator-completions/<term>?response_format=json`
Substiture `<term>` with the name (or part of the name) of the operator you want to search for.

### Example operator completions response

The operator completions will return an array of results and each result will contain a `name` and `id` key.

```json
[
  {
    "name": "Some operator, Inc",
    "id": 1
  }
]
```

## NeTEx

NeTEx is a CEN Technical Standard for exchanging Public Transport schedules and related data.

Service converts GTFS and Kalkati.net packages to NeTEx.
Make query using service search api and search external-interface-links - external-interface object. 
NeTEx data can be downloaded from url that has is in "NeTEx" format.