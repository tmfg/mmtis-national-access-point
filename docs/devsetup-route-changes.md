How to populate route changes view in dev setup. (Note to audience following along at home: The invoked functions from JS console are calling the backend through normal admin access checks, just like other frontend operations).

1. create transit service
2. scheduled service, ok to use existing provider
3. fill out mandatory fields (primary operating area, address, etc)
4. go to production site and look for api contents with scheduled routes
5. find gtfs link and paste it to your dev setup creation page, selecting the matching data conten type (eg gtfs)
6. go to your JS console
7. invoke ote.app.controller.admin.force_interface_import();
8. inspect your db to verify you have a record of the import:
  select * from "external-interface-description";
9. change the gtfs-imported date to effect a reload, for example
  update "external-interface-description" set "gtfs-imported" = '2017-11-12 11:01:18.177411+00';
10. in JS console:
 ote.app.controller.admin.force_detect_transit_changes();
 
