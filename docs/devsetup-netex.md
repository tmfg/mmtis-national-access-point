### How to setup netex conversion in development environment

1. Ensure that AWS S3 bucket is created
    1. There is three options so choose the correct one
2. Run netex initialization bash script from tools/chouette
    1. sh initialize-chouette.sh
3. Test that conversion works
    1. Create service with correct gtfs url (see more from devsetup-route-changes.md)
    2. Download gtfs package using admin panel -> this starts netex conversion
    3. In repl log should be indications if everyting is ok    
4. Run Unit Tests - there is tests for Netex conversions also
  