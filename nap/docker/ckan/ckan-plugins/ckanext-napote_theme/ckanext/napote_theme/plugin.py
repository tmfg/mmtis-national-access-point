# encoding: utf-8

import ckan.plugins as plugins
import ckan.plugins.toolkit as tk
from ckan.lib.plugins import DefaultTranslation

# TODO: We should get these from the database
municipalitys = (
    u'Helsinki', u'Ii', u'Joensuu', u'Kempele', u'Muhos', u'Oulu', u'Pieksämäki', u'Salo', u'Seinäjoki', u'Vantaa')
transport_services = (u'Terminal', u'Passenger Transportation', u'Rental', u'Parking', u'Brokerage')


def create_transport_service_types():
    user = tk.get_action('get_site_user')({'ignore_auth': True}, {})
    context = {'user': user['name']}

    try:
        data = {'id': 'transport_service_types'}
        tk.get_action('vocabulary_show')(context, data)
    except tk.ObjectNotFound:
        data = {'name': 'transport_service_types'}
        vocab = tk.get_action('vocabulary_create')(context, data)

        for tag in transport_services:
            data = {'name': tag, 'vocabulary_id': vocab['id']}
            tk.get_action('tag_create')(context, data)


def transport_service_types():
    create_transport_service_types()

    try:
        tag_list = tk.get_action('tag_list')
        service_types = tag_list(data_dict={'vocabulary_id': 'transport_service_types'})
        return service_types
    except tk.ObjectNotFound:
        return None


def create_mock_operation_areas():
    user = tk.get_action('get_site_user')({'ignore_auth': True}, {})
    context = {'user': user['name']}

    try:
        data = {'id': 'operation_areas'}
        tk.get_action('vocabulary_show')(context, data)
    except tk.ObjectNotFound:
        data = {'name': 'operation_areas'}
        vocab = tk.get_action('vocabulary_create')(context, data)

        for tag in municipalitys:
            data = {'name': tag, 'vocabulary_id': vocab['id']}
            tk.get_action('tag_create')(context, data)


def operation_areas():
    create_mock_operation_areas()

    try:
        tag_list = tk.get_action('tag_list')
        service_types = tag_list(data_dict={'vocabulary_id': 'operation_areas'})
        return service_types
    except tk.ObjectNotFound:
        return None


def tags_to_select_options(tags=None):
    if tags is None:
        tags = []
    return [{'name': tag, 'value': tag} for tag in tags]


class NapoteThemePlugin(plugins.SingletonPlugin, tk.DefaultDatasetForm):
    # http://docs.ckan.org/en/latest/extensions/translating-extensions.html
    # Enable after translations have been generated
    # plugins.implements(plugins.ITranslation)
    plugins.implements(plugins.IConfigurer)
    plugins.implements(plugins.IDatasetForm)
    plugins.implements(plugins.ITemplateHelpers)

    def get_helpers(self):
        return {
            'transport_service_types': transport_service_types,
            'operation_areas': operation_areas,
            'tags_to_select_options': tags_to_select_options}

    def update_config(self, config):
        # Add this plugin's templates dir to CKAN's extra_template_paths, so
        # that CKAN will use this plugin's custom templates.
        # 'templates' is the path to the templates dir, relative to this
        # plugin.py file.
        tk.add_template_directory(config, 'templates')

        # Register this plugin's fanstatic directory with CKAN.
        # Here, 'fanstatic' is the path to the fanstatic directory
        # (relative to this plugin.py file), and 'napote_theme' is the name
        # that we'll use to refer to this fanstatic directory from CKAN
        # templates.
        tk.add_resource('fanstatic', 'napote_theme')

        # Public directory for static images
        tk.add_public_directory(config, 'public')


    def _modify_package_schema(self, schema):
        # add custom fields
        schema.update({
            'transport_service_type': [tk.get_validator('ignore_missing'),
                                       tk.get_converter('convert_to_tags')('transport_service_types')]
        })

        schema.update({
            'operation_area': [tk.get_validator('ignore_missing'),
                               tk.get_converter('convert_to_tags')('operation_areas')]
        })

        return schema


    def show_package_schema(self):
        schema = super(NapoteThemePlugin, self).show_package_schema()

        # Prevent listing vocabulary tags mixed in with normal tags
        schema['tags']['__extras'].append(tk.get_converter('free_tags_only'))

        schema.update({
            'transport_service_type': [tk.get_converter('convert_from_tags')('transport_service_types'),
                                       tk.get_validator('ignore_missing')],
        })

        schema.update({
            'operation_area': [tk.get_converter('convert_from_tags')('operation_areas'),
                               tk.get_validator('ignore_missing')]
        })

        return schema

    def create_package_schema(self):
        schema = super(NapoteThemePlugin, self).create_package_schema()
        schema = self._modify_package_schema(schema)

        return schema

    def update_package_schema(self):
        schema = super(NapoteThemePlugin, self).update_package_schema()
        schema = self._modify_package_schema(schema)

        return schema

    def is_fallback(self):
        return True

    def package_types(self):
        return []
