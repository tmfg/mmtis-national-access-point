# encoding: utf-8

import ckan.plugins as plugins
import ckan.plugins.toolkit as toolkit
from ckan.lib.plugins import DefaultTranslation


class NapoteThemePlugin(plugins.SingletonPlugin, toolkit.DefaultDatasetForm):
    # http://docs.ckan.org/en/latest/extensions/translating-extensions.html
    # Enable after translations have been generated
    #plugins.implements(plugins.ITranslation)
    plugins.implements(plugins.IConfigurer)
    plugins.implements(plugins.IDatasetForm)

    def update_config(self, config):
        # Add this plugin's templates dir to CKAN's extra_template_paths, so
        # that CKAN will use this plugin's custom templates.
        # 'templates' is the path to the templates dir, relative to this
        # plugin.py file.
        toolkit.add_template_directory(config, 'templates')

        # Register this plugin's fanstatic directory with CKAN.
        # Here, 'fanstatic' is the path to the fanstatic directory
        # (relative to this plugin.py file), and 'napote_theme' is the name
        # that we'll use to refer to this fanstatic directory from CKAN
        # templates.
        toolkit.add_resource('fanstatic', 'napote_theme')

        # Public directory for static images
        toolkit.add_public_directory(config, 'public')

    def nap_package_schema(self):
        # Take default schema
        schema = super(NapoteThemePlugin, self).create_package_schema()
        # add custom fields
        schema.update({
            'transport_service_type': [tk.get_validator('ignore_missing'),
                                       tk.get_converter('convert_to_extras')],
            'operation_area': [tk.get_validator('ignore_missing'),
                               tk.get_converter('convert_to_extras')]
        })
        return schema

    def create_package_schema(self):
        return self.nap_package_schema()

    def update_package_schema(self):
        return self.nap_package_schema()

    def show_package_schema(self):
        schema = super(NapoteThemePlugin, self).show_package_schema()
        schema.update({
            'transport_service_type': [tk.get_converter('convert_from_extras'),
                                       tk.get_validator('ignore_missing')],
            'operation_area': [tk.get_converter('convert_from_extras'),
                               tk.get_converter('ignore_missing')]
        })
        return schema
