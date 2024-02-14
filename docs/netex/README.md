# NeTEx conversion documentation

## References
[finlex-nap]: http://www.finlex.fi/fi/laki/alkup/2017/20170320 "Laki liikenteen palveluista"
[tis-vaco]: https://www.fintraffic.fi/fi/fintraffic/validointi-ja-konvertointipalvelu "Validointi- ja 
konvertointipalvelu"
[netex-nordic]: https://enturas.atlassian.net/wiki/spaces/PUBLIC/pages/728891481/Nordic+NeTEx+Profile Nordic NeTEx Profile

- http://netex-cen.eu
- http://www.transmodel-cen.eu
- https://github.com/NeTEx-CEN/NeTEx

## Description

NeTEx is a CEN Technical Standard for exchanging Public Transport schedules and related data.
> Although NeTEx is a large standard, a NeTEx service needs only to implement the specific elements relevant to 
its business objectives – extraneous elements present in the model can be ignored. Parties using NeTEx for a 
particular purpose will typically define a “profile” to identify the subset of elements that must be present, 
as well as the code sets to be used to identify them.


### Need

Provide data of scheduled traffic in [Nordic NeTEx Profile](netex-nordic).

## Datamodel

Related database tables:
- `netex-conversion`: each record represents the result of conversion run for an interface

## Functionality
Finnish NAP uses [Fintraffic's Validation and Conversion service](tis-vaco) to convert transport services' listed 
external GTFS interfaces into NeTEx format. The conversion is done entirely by the external service.


![Use-case overview](netex_use_cases_overview.png)
