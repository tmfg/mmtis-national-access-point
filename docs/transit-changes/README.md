# Transit changes documentation

All changes in transit traffic must be informed to the transit authorities 60 days in advance.
If the data is available from machine readable interfaces, changes will be automatically detected
and transit authorities notified. If a proper interface is not available for a transit service, the
changes must be notified by filling out a manual form.

To facilitate automatic change detection, NAP reads existing transit route and schedule information
from interfaces provided by transport operators and stores them.



## Data gathering

### Interfaces

![add new interface](new-interface.png)

Interfaces are added by transport operators to their service info using the service form.
Currently, we support [GTFS](https://developers.google.com/transit/gtfs/) and [Kalkati.net](http://developer.matka.fi/pages/en/kalkati.net-xml-database-dump.php) formatted interfaces for route and schedule information.


### Storage

Interface data is always stored in GTFS format. We archive the raw GTFS zip files in AWS S3 bucket. This enables us to fetch older historical data if so required in the future.
During the import process, GTFS files are parsed and stored into our relational database model in AWS RDS. The model is not exact mirror of the GTFS standard, but it is logically very similar.
It contains all the same information. GTFS import process does not support other optional GTFS files than calendar_dates.txt and shapes.txt.


### Background process

![background import process](import-process.png)

The import process described above runs every night during night hours. The process tries to update any interface data that has not been updated during 24 hours.
The process queries the interface server for new data. If the server indicates that the data has changed, a new package will be downloaded and processed.
For this process we utilize `If-None-Match` or `If-Modified-Since` HTTP headers.
Relevant package meta data, such as ETAG and current timestamp is stored during this process.

If there are any errors encountered while fetching the new package or during the import process, the interface will be marked as erroneous.
First, we check that if the interface URL can be connected to. Then we download the package and check that the downloaded file is a ZIP file and if it contains all the required GTFS or Kalkati.net files depending on the specified interface format. 


Kalkati.net formatted interfaces are converted into GTFS before storing and importing the data. 
For this, we utilize a kalkati->gtfs converter that parses a Kalkati.net XML-file and outputs a GTFS zip.


![Database model](db-diagram.svg)

Above is a basic diagram about database tables related to the transit changes process.


## Change detection


### Detection algorithm

### Background process

![background detection process](detection-process.png)


### Notifications
