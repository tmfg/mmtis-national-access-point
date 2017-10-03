#!/bin/sh

set -e

# URL for the primary database, in the format expected by sqlalchemy (required
# unless linked to a container called 'db')
: ${CKAN_SQLALCHEMY_URL:=}
# URL for solr (required unless linked to a container called 'solr')
: ${CKAN_SOLR_URL:=}
# URL for redis (required unless linked to a container called 'redis')
: ${CKAN_REDIS_URL:=}

CONFIG="${CKAN_CONFIG}/ckan.ini"

abort () {
  echo "$@" >&2
  exit 1
}

set_environment () {
  export CKAN_SQLALCHEMY_URL=${CKAN_SQLALCHEMY_URL}
  export CKAN_SOLR_URL=${CKAN_SOLR_URL}
  export CKAN_REDIS_URL=${CKAN_REDIS_URL}
  export CKAN_STORAGE_PATH=${CKAN_STORAGE_PATH}
  export CKAN_SITE_URL=${CKAN_SITE_URL}
}

write_config () {
  # http://docs.ckan.org/en/latest/maintaining/configuration.html
  # Note that this only gets called if there is no config, see below!
  ckan-paster make-config --no-interactive ckan "$CONFIG"

  # The environment variables above will be used by CKAN, but
  # you can create a custon ckan.ini like this also:
  ckan-paster --plugin=ckan config-tool "$CONFIG" -e \
    "ckan.plugins = stats text_view image_view recline_view napote_theme" \
    "ckan.locale_default = fi" \
    "ckan.locale_order = fi en sv" \
    "ckan.locales_offered = fi en sv" \
    "ckan.locales_filtered_out = en_GB"
  #    "sqlalchemy.url = ${CKAN_SQLALCHEMY_URL}" \
  #    "solr_url = ${CKAN_SOLR_URL}" \
  #    "ckan.redis.url = ${CKAN_REDIS_URL}" \
  #    "ckan.storage_path = ${CKAN_STORAGE_PATH}" \
  #    "ckan.site_url = ${CKAN_SITE_URL}"
}


# If we don't already have a config file, bootstrap
if [ ! -e "$CONFIG" ]; then
  write_config
fi

# Set environment variables
if [ -z "$CKAN_SQLALCHEMY_URL" ]; then
    abort "ERROR: No CKAN_SQLALCHEMY_URL specified."
else
    # Wait for postgres to be available. This can take a while.
    for tries in $(seq 30); do
      psql -c 'SELECT 1;' 2> /dev/null && break
      sleep 0.3
    done
fi

if [ -z "$CKAN_SOLR_URL" ]; then
    abort "ERROR: No CKAN_SOLR_URL specified."
fi

if [ -z "$CKAN_REDIS_URL" ]; then
    abort "ERROR: No CKAN_REDIS_URL specified."
fi

set_environment

# Initializes the Database
ckan-paster --plugin=ckan db init -c "${CKAN_CONFIG}/ckan.ini"

exec "$@"