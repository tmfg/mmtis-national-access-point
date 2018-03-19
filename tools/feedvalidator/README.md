# Docker image of google GTFS feedvalidator

## Building

You can use the image in dockerhub (`solita/gtfs-feedvalitor`) or run `build.sh` to build locally.

## Running

Make sure you have the gtfs file as `gtfs.zip` in the current directory and run `validate.sh`.
The run will generate an HTML page in `output.html` which contains the validation errors and warnings.
