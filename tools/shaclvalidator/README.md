# Docker image for SHACL validation using Apache Jena

## Building

You can run `build.sh` to build the image locally as `solita/shacl-validator`.

## Running

Run `validate.sh` to validate RDF data against MobilityDCAT-AP SHACL shapes.

The script will automatically download required files if they don't exist:
- **shapes.ttl** - Downloaded from the official MobilityDCAT-AP 1.1.0 SHACL shapes if not present
- **data.ttl** - Downloaded from `http://localhost:3000/rdf` if not present

This means you can simply run `./validate.sh` without any preparation, and it will fetch both the shapes and your application's RDF data automatically.

### Automatic Fixes Applied to shapes.ttl

When the shapes file is downloaded, the following fixes are automatically applied:

1. **GitHub issue #131** - Updates the access-right URL to licence URL
   - Changes `http://publications.europa.eu/resource/authority/access-right` to `http://publications.europa.eu/resource/authority/licence`
   - See: https://github.com/mobilityDCAT-AP/mobilityDCAT-AP/issues/131

2. **DCAT-AP issue #403** - Adds the missing `shacl` prefix at the beginning of the file
   - See: https://github.com/SEMICeu/DCAT-AP/issues/403

3. **owl:imports removal** - Removes all lines containing `owl:imports` from the shapes file
   - These imports result in mutually contradictory validation rules with the current validation setup

### Manual File Preparation

If you want to validate specific files, you can create them manually before running the script:
- Create `shapes.ttl` to use custom SHACL shapes
- Create `data.ttl` to validate a specific RDF file

### Manual Usage

You can also run the container directly:

```bash
docker run -v $(pwd):/data solita/shacl-validator validate --shapes /data/shapes.ttl --data /data/data.ttl
```


