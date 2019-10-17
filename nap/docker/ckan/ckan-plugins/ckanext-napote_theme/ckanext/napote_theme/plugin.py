# encoding: utf-8

import smtplib
import socket
import mimetypes
from time import time
from email.mime.text import MIMEText
from email.header import Header
from email import Utils
from logging import getLogger
import csv
import sys

import ckan.logic as logic
import ckan.model as model
import ckan.authz as authz
import ckan.plugins as plugins
import ckan.plugins.toolkit as tk
from ckan.lib.plugins import DefaultTranslation
from routes.mapper import SubMapper
from ckan.lib import authenticator
import paste.deploy.converters
from ckan.lib import mailer
from ckan.common import c, _, config
from ckan.model import User

from ckan.lib import i18n
from ckan.plugins import PluginImplementations
from ckan.plugins.interfaces import ITranslation
from paste.deploy.converters import aslist

csv.field_size_limit(sys.maxsize)

log = getLogger(__name__)


### FIXME: MONKEY PATCH CKAN AUTHENTICATION START ###

def authenticate_monkey_patch(self, environ, identity):
    """Using our custom authentication method with Repoze auth
    This allows us to use email address in login """
    if not ('login' in identity and 'password' in identity):
        return None

    login = identity['login']

    # Try login with email
    users = User.by_email(login)

    # If we get an user list as a result, use the first result. Otherwise, try to
    # get user by username.
    if users:
        # Use the first result only. We'll have to assume that that we have one email per username
        # in the ckan database. By default, CKAN allows using the same email for multiple users.
        user = users[0]
    else:
        user = User.by_name(login)

    if user is None:
        log.debug('Login failed - username %r not found', login)
    elif not user.is_active():
        log.debug('Login as %r failed - user isn\'t active', login)
    elif not user.validate_password(identity['password']):
        log.debug('Login as %r failed - password not valid', login)
    else:
        return user.name

    return None


authenticator.UsernamePasswordAuthenticator.authenticate = authenticate_monkey_patch

### MONKEY PATCH CKAN AUTHENTICATION END ###

### MONKEY PATCH CKAN I18N LANGUAGE START ###

supported_languages = ['fi', 'sv', 'en']


# Take language from "finap_lang" cookie which both CKAN and OTE
# can use. Language selection sets cookie and refreshes the page.
def language_from_cookie(request):
    lang = "fi"
    try:
        lang = request.cookies['finap_lang']
    except:
        pass

    return lang


def handle_request(request, tmpl_context):
    ''' Set the language for the request '''

    config = i18n.config

    lang = language_from_cookie(request)
    i18n.set_lang(lang)

    ## CODE AFTER THIS LINE IS TAKEN AS IS FROM ckan.lib.i18n
    for plugin in PluginImplementations(ITranslation):
        if lang in plugin.i18n_locales():
            i18n._add_extra_translations(plugin.i18n_directory(), lang,
                                         plugin.i18n_domain())

    extra_directory = config.get('ckan.i18n.extra_directory')
    extra_domain = config.get('ckan.i18n.extra_gettext_domain')
    extra_locales = aslist(config.get('ckan.i18n.extra_locales'))
    if extra_directory and extra_domain and extra_locales:
        if lang in extra_locales:
            i18n._add_extra_translations(extra_directory, lang, extra_domain)

    tmpl_context.language = lang
    return lang


i18n.handle_request = handle_request


### MONKEY PATCH CKAN I18N LANGAUGE END ###



### MONKEY PATCH CKAN MAILER USER MAIL SEND START ###

def _mail_recipient_monkey_patch(recipient_name, recipient_email,
                    sender_name, sender_url, subject,
                    body, headers={}):
    mail_from = config.get('smtp.mail_from')
    msg = MIMEText(body.encode('utf-8'), 'plain', 'utf-8')
    for k, v in headers.items():
        msg[k] = v
    subject = Header(subject.encode('utf-8'), 'utf-8')
    msg['Subject'] = subject
    msg['From'] = _("%s <%s>") % (sender_name, mail_from)
    # NOTE: Removed reciepient_name
    recipient = u"%s" % (recipient_email)
    msg['To'] = Header(recipient, 'utf-8')
    msg['Date'] = Utils.formatdate(time())

    # Send the email using Python's smtplib.
    smtp_connection = smtplib.SMTP()
    if 'smtp.test_server' in config:
        # If 'smtp.test_server' is configured we assume we're running tests,
        # and don't use the smtp.server, starttls, user, password etc. options.
        smtp_server = config['smtp.test_server']
        smtp_starttls = False
        smtp_user = None
        smtp_password = None
    else:
        smtp_server = config.get('smtp.server', 'localhost')
        smtp_starttls = paste.deploy.converters.asbool(
            config.get('smtp.starttls'))
        smtp_user = config.get('smtp.user')
        smtp_password = config.get('smtp.password')

    try:
        smtp_connection.connect(smtp_server)
    except socket.error, e:
        log.exception(e)
        raise mailer.MailerException('SMTP server could not be connected to: "%s" %s'
                              % (smtp_server, e))
    try:
        # Identify ourselves and prompt the server for supported features.
        smtp_connection.ehlo()

        # If 'smtp.starttls' is on in CKAN config, try to put the SMTP
        # connection into TLS mode.
        if smtp_starttls:
            if smtp_connection.has_extn('STARTTLS'):
                smtp_connection.starttls()
                # Re-identify ourselves over TLS connection.
                smtp_connection.ehlo()
            else:
                raise mailer.MailerException("SMTP server does not support STARTTLS")

        # If 'smtp.user' is in CKAN config, try to login to SMTP server.
        if smtp_user:
            assert smtp_password, ("If smtp.user is configured then "
                                   "smtp.password must be configured as well.")
            smtp_connection.login(smtp_user, smtp_password)

        smtp_connection.sendmail(mail_from, [recipient_email], msg.as_string())
        log.info("Sent email to {0}".format(recipient_email))

    except smtplib.SMTPException, e:
        msg = '%r' % e
        log.exception(msg)
        raise mailer.MailerException(msg)
    finally:
        smtp_connection.quit()


mailer._mail_recipient = _mail_recipient_monkey_patch

### MONKEY PATCH CKAN MAILER USER MAIL SEND END###


### Custom auth functions ###

def dataset_purge_custom_auth(context, data_dict):
    # Defer authorization for package_pruge to package_update
    # This authorization is similar to editing package fields.

    return authz.is_authorized('package_update', context, data_dict)


### HELPERS ####

def log_debug(*args):
    log.info(*args)


def tags_to_select_options(tags=None):
    if tags is None:
        tags = []
    return [{'name': tag, 'value': tag} for tag in tags]


def get_in(data, *keys):
    try:
        for k in keys:
            data = data[k]
        return data
    except (IndexError, KeyError) as e:
        return None


def user_orgs(user_name, permission='manage_group', include_dataset_count=False):
    context = {'model': model, 'user': c.user}
    data_dict = {
        'id': user_name,
        'permission': permission,
        'include_dataset_count': include_dataset_count}
    return logic.get_action('organization_list_for_user')(context, data_dict)


# TODO: This is an example, how to translate our dataset fields. ckan multilingual needs to be added into ckan.plugins for this to work.
def update_term_translations():
    return tk.get_action('term_translation_update_many')({'ignore_auth': True}, {
        'data': [
            {
                'term': u'passenger-transportation',
                'term_translation': u'Henkilöidenkuljetuspalvelut',
                'lang_code': 'fi'
            },
            {
                'term': 'terminal',
                'term_translation': u'Asemat, satamat ja muut terminaalit',
                'lang_code': 'fi'
            },
            {
                'term': 'rentals',
                'term_translation': u'Ajoneuvojen vuokrauspalvelut ja kaupalliset yhteiskäyttöpalvelut',
                'lang_code': 'fi'
            },
            {
                'term': 'parking',
                'term_translation': u'Yleiset kaupalliset pysäköintipalvelut',
                'lang_code': 'fi'
            },
            {
                'term': 'brogerake',
                'term_translation': u'Välityspalvelut',
                'lang_code': 'fi'
            }
        ]})


class NapoteThemePlugin(plugins.SingletonPlugin, DefaultTranslation, tk.DefaultDatasetForm):
    plugins.implements(plugins.IAuthFunctions)
    plugins.implements(plugins.IPluginObserver, inherit=True)
    plugins.implements(plugins.IConfigurer)
    plugins.implements(plugins.IRoutes, inherit=True)

    # http://docs.ckan.org/en/latest/extensions/translating-extensions.html
    plugins.implements(plugins.ITranslation)
    plugins.implements(plugins.IDatasetForm)
    plugins.implements(plugins.ITemplateHelpers)
    plugins.implements(plugins.IFacets, inherit=True)
    plugins.implements(plugins.IResourceView, inherit=True)

    def get_auth_functions(self):
        return {'dataset_purge': dataset_purge_custom_auth}

    def get_helpers(self):
        return {
            'tags_to_select_options': tags_to_select_options,
            'log_debug': log_debug,
            'user_orgs': user_orgs}

    def after_load(self, service):
        # update_term_translations()
        return service

    def update_config(self, config):
        # CKAN uses the default Python library mimetypes to detect the media type of afile.
        # If some particular format is not included in the ones guessed by the mimetypes library,
        # a default application/octet-stream value will be returned.

        # Add support for svg files in templates.
        mimetypes.add_type('image/svg+xml', '.svg')

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

    def before_map(self, map):
        map.redirect('/dataset/new', '/error/')
        map.redirect('/dataset/edit/{id:.*}', '/error/')
        map.redirect('/dataset/groups/{id:.*}', '/error/')
        map.redirect('/dataset/delete/{id:.*}', '/error/')
        map.redirect('/dataset/new_resource/{id:.*}', '/error/')
        map.redirect('/dataset/{id:.*}/resource/{resource_id:.*}/new_view', '/error/')
        map.redirect('/dataset/{id:.*}/resource_edit/{resource_id:.*}', '/error/')
        map.redirect('/dataset/{id:.*}/resource/{resource_id:.*}/edit_view/{view_id:.*}', '/error/')
        map.redirect('/dataset/{id:.*}/resource_delete/{resource_id:.*}', '/error/')
        map.redirect('/group/new', '/error/')
        map.redirect('/group/member_new/{id:.*}', '/error/')
        map.redirect('/group/edit/{id:.*}', '/error/')
        map.redirect('/organization/bulk_process/{id:.*}/', '/error/')
        map.redirect('/user/activity/{url:.*}', '/error/')

        # Hook user password reset route to our custom user controller

        map.connect('/user/reset',
                    controller='ckanext.napote_theme.controller:CustomUserController',
                    action='request_reset')

        map.connect('/user/register',
                    controller='ckanext.napote_theme.controller:CustomUserController',
                    action='register')

        map.connect('/organization/new',
                    controller='ckanext.napote_theme.organization_controller:CustomOrganizationController',
                    action='new')

        map.connect('/organization/member_new/{id}',
                    controller='ckanext.napote_theme.organization_controller:CustomOrganizationController',
                    action='member_new')

        map.connect('/user/login',
                    controller='ckanext.napote_theme.controller:CustomUserController',
                    action='login')

        map.connect('/user/logged_in',
                    controller='ckanext.napote_theme.controller:CustomUserController',
                    action='logged_in')

        map.connect('/user/_logout',
                    controller='ckanext.napote_theme.controller:CustomUserController',
                    action='logout')

        map.connect('/user/edit',
                    controller='ckanext.napote_theme.controller:CustomUserController',
                    action='edit')
        map.connect('/user/edit/{id}',
                    controller='ckanext.napote_theme.controller:CustomUserController',
                    action='edit')

        map.connect('/user/{id}',
                    controller='')

        return map

    def after_map(self, map):
        with SubMapper(map, controller='package') as m:
            m.connect('search', '/ote/#/services', action='search',
                      highlight_actions='index search')
        return map

    def dataset_facets(self, facets_dict, package_type):
        facets_dict.clear()

        # facets_dict['organization'] = tk._('Organizations')
        facets_dict['extras_transport_service_type'] = tk._('Transport Service Type')
        # facets_dict['extras_operation_area'] = tk._('Operation Area')
        # facets_dict['tags'] = tk._('Tags')
        # facets_dict['res_format'] = tk._('Formats')
        # facets_dict['license_id'] = tk._('Licenses')

        return facets_dict

    def organization_facets(self, facets_dict, organization_type, package_type):
        facets_dict.clear()

        facets_dict['extras_transport_service_type'] = tk._('Transport Service Type')
        # facets_dict['extras_operation_area'] = tk._('Operation Area')
        # facets_dict['tags'] = tk._('Tags')
        # facets_dict['res_format'] = tk._('Formats')
        # facets_dict['license_id'] = tk._('Licenses')

        return facets_dict

    def _modify_package_schema(self, schema):
        # add custom fields
        schema.update({
            'transport_service_type': [tk.get_validator('ignore_missing'),
                                       tk.get_converter('convert_to_extras')]
        })

        schema.update({
            'operation_area': [tk.get_validator('ignore_missing'),
                               tk.get_converter('convert_to_extras')]
        })

        return schema

    def show_package_schema(self):
        schema = super(NapoteThemePlugin, self).show_package_schema()

        # Prevent listing vocabulary tags mixed in with normal tags
        schema['tags']['__extras'].append(tk.get_converter('free_tags_only'))

        schema.update({
            'transport_service_type': [tk.get_converter('convert_from_extras'),
                                       tk.get_validator('ignore_missing')],
        })

        schema.update({
            'operation_area': [tk.get_converter('convert_from_extras'),
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

    # Methods for IResourceView

    def info(self):
        return {'name': 'transport_service_view',
                'title': tk._('Transport Service View'),
                'iframed': False}

    def can_view(self, data_dict):
        return data_dict['resource']['format'] == 'GeoJSON'

    def setup_template_variables(self, context, data_dict):
        # log_debug('setup_template_variables, ctx:\n %s, data:\n %s', pprint.pformat(context), pprint.pformat(data_dict))
        url = get_in(data_dict, 'resource', 'url') or get_in(data_dict, 'package', 'resources', 0, 'url')
        return {'transport_service_url': url}

    def view_template(self, context, data_dict):
        return "transport_service_view.html"

    def form_template(self, context, data_dict):
        return "transport_service_view.html"