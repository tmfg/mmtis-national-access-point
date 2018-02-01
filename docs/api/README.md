# FINAP service catalog API

FINAP backend exposes the same query interface as the application uses to outside developers.
It can be used without authentication.

## Parameters

The query is a GET request and all the search filters are given as query parameters.

Name | Description | Example
---- | ----------- | -------
text | Free text search term (searches the name of the service) | `bus`
operation_area | Comma separated list, matches services that have any of the listed areas as their operation area | `Oulu,Kempele`
sub_types | Comma separated list, matches services of the listed type (see service types) | `terminal,taxi`
operators | List of operator ids (see operator search), matches any service created by the given operators | `4,8,15,16,23,42`
limit | Max number of results to return | `25`
offset | Where to start returning results (for paging) | `0`
format | Output format (defaults to transit) | `json`

All parameters are optional. If no parameters are specified the search will return the latest services.
The default output format is Transit but can be changed to JSON with the `format` query parameter.

## Service types

There are 7 service types (4 main types with 4 sub types for passenger transportation).
All the types are represented in the "sub_types" parameter list.

Valid values are

Value | Description
----- | -----------
taxi | Taxi (passenger transportation)
request | Request based (passenger transportation)
schedule | Regularly scheduled (passenger transportation)
other | Other (passenger transportation)
terminal | Terminals and stations
rentals | Vehicle rental services
parking | Parking areas


## Response

The following shows an example of the response in the JSON format:

```json
{
  "empty-filters?": false,
  "total-service-count": 123,
  "results": [
    {
      "contact-address": {
        "street": "Some street 1",
        "postal_code": "112233",
        "post_office": "Somewhere"
      },
      "transport-operator-id": 1,
      "type": "terminal",
      "operator-name": "Some operator, Inc",
      "name": "The bus stop to somewhere",
      "sub-type": "terminal",
      "id": 2
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
