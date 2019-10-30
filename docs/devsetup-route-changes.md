### How to populate route changes view in dev setup. 

1. Create transport operator
2. Create transit service for operator 
    1. Select Regular scheduled service
    2. Fill out mandatory fields (primary operating area, address, etc)
    3. Add External interface
        1. Data content: Route and schedule information
        2. Add WWW-address e.g. go to production site and look for api contents with scheduled routes
        3. Set format as GTFS
3. Save service and take note of the service id        
4. Go to Admin panel
5. Add service id and click "Lataa palveluun liitetty gtfs paketti" 
6. Add service id and click "Muutostunnistus yhdelle palvelulle"
7. Go to "Tunnistetut muutokset" tab and inspect results 
 
