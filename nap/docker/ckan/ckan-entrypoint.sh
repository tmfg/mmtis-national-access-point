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
  # Default host: 0.0.0.0, default port: 5000
  ckan-paster --plugin=ckan config-tool "$CONFIG" -s server:main -e \
    "host = 0.0.0.0" \
    "port = 5000"

  ckan-paster --plugin=ckan config-tool "$CONFIG" -s app:main -e \
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

watch_plugin_changes () {
  echo "Watching plugin source file changes at: $CKAN_CUSTOM_PLUGINS_PATH ..."

  inotifywait -q -m -r -e close_write,delete,move \
    --exclude \___jb_ --format '%w%f' $CKAN_CUSTOM_PLUGINS_PATH | \
    while read FILE_PATH
     do
      echo "Plugins source file changed: $FILE_PATH. Updating plugin..."

      # Remove the custom plugins volume path from the beginning of the file path
      # and append the result to ckan home src path.
      TARGET_PATH="$CKAN_HOME/src/${FILE_PATH/#"$CKAN_CUSTOM_PLUGINS_PATH"\/}"
      cp --verbose $FILE_PATH $TARGET_PATH

      # Remove everything from the target path starting from /ckanext/ to
      # to get the main path of the plugin that has changed files.
      PLUGIN_MAIN_PATH=${TARGET_PATH%%/ckanext/*}

      # Update the plugin by reinstalling it. The setup.py file of the plugin is located
      # in the plugin main path.
      # NOTE: We'll see if this is required at all. Some changes might require reinstalling the plugin.
      #   Enable this if required.
      # ckan-pip install -e $PLUGIN_MAIN_PATH
    done &
}

# Write config
# NOTE: Currently we are re-creating the config each time to make sure that we have a proper config file
#   after changing settings.
if [ -e "$CONFIG" ]; then
  echo "Removing old ckan.ini..."

  rm $CONFIG
fi

write_config


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

# Start watching custom plugin changes in /ckan-plugins volume
watch_plugin_changes

exec "$@"