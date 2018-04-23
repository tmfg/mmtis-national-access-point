# encoding: utf-8

import logging
import ckan
import ckan.lib.base as base
import ckan.model as model
import ckan.lib.helpers as h
import ckan.logic as logic
import ckan.logic.schema as schema
import ckan.lib.captcha as captcha
import ckan.lib.mailer as mailer
import ckan.plugins as p
import ckan.lib.authenticator as authenticator

import ckan.lib.navl.dictization_functions as dictization_functions

from ckan.common import _, c, request, response, config
from paste.deploy.converters import asbool

from ckan.controllers.user import UserController

from validators import email_uniq_validator

set_repoze_user = ckan.controllers.user.set_repoze_user

log = logging.getLogger(__name__)

abort = base.abort
render = base.render

check_access = logic.check_access
get_action = logic.get_action
NotFound = logic.NotFound
NotAuthorized = logic.NotAuthorized
ValidationError = logic.ValidationError
UsernamePasswordError = logic.UsernamePasswordError

DataError = dictization_functions.DataError
unflatten = dictization_functions.unflatten


class CustomUserController(UserController):
    def _new_form_to_db_schema(self):
        user_new_form_schema = schema.user_new_form_schema()

        # Add custom validators
        user_new_form_schema['email'].append(email_uniq_validator)

        return user_new_form_schema

    def _db_to_new_form_schema(self):
        '''This is an interface to manipulate data from the database
        into a format suitable for the form (optional)'''

    def _edit_form_to_db_schema(self):
        user_edit_form_schema = schema.user_edit_form_schema()

        # Add custom validators
        user_edit_form_schema['email'].append(email_uniq_validator)

        return user_edit_form_schema

    def _save_new(self, context):
        try:
            data_dict = logic.clean_dict(unflatten(
                logic.tuplize_dict(logic.parse_params(request.params))))
            context['message'] = data_dict.get('log_message', '')
            captcha.check_recaptcha(request)
            user = get_action('user_create')(context, data_dict)
        except NotAuthorized:
            abort(403, _('Unauthorized to create user %s') % '')
        except NotFound, e:
            abort(404, _('User not found'))
        except DataError:
            abort(400, _(u'Integrity Error'))
        except captcha.CaptchaError:
            error_msg = _(u'Bad Captcha. Please try again.')
            h.flash_error(error_msg)
            return self.new(data_dict)
        except ValidationError, e:
            errors = e.error_dict
            error_summary = e.error_summary
            return self.new(data_dict, errors, error_summary)
        if not c.user:
            # log the user in programatically
            set_repoze_user(data_dict['name'])
            return render('user/ote_close.html')
        else:
            # #1799 User has managed to register whilst logged in - warn user
            # they are not re-logged in as new user.
            h.flash_success(_('User "%s" is now registered but you are still '
                            'logged in as "%s" from before') %
                            (data_dict['name'], c.user))
            if authz.is_sysadmin(c.user):
                # the sysadmin created a new user. We redirect him to the
                # activity page for the newly created user
                h.redirect_to(controller='user',
                              action='activity',
                              id=data_dict['name'])
            else:
                return render('user/logout_first.html')

    def register(self, data=None, errors=None, error_summary=None):
        return super(CustomUserController, self).register(data, errors, error_summary)

    def request_reset(self):
        context = {'model': model, 'session': model.Session, 'user': c.user,
                   'auth_user_obj': c.userobj}

        try:
            check_access('request_reset', context)
        except NotAuthorized:
            abort(403, _('Unauthorized to request reset password.'))

        if request.method == 'POST':
            # Try to get username with email address
            users = model.User.by_email(request.params.get('user'))

            # If we get an user list as a result, use the first result. Otherwise, try to
            # get user by username.
            if users:
                # Use the first result only. We'll have to assume that that we have one email per username
                # in the ckan database. By default, CKAN allows using the same email for multiple users.
                id = users[0].name
            else:
                id = request.params.get('user')

            context = {'model': model,
                       'user': c.user}

            data_dict = {'id': id}
            user_obj = None
            try:
                user_dict = get_action('user_show')(context, data_dict)
                user_obj = context['user_obj']
            except NotFound:
                h.flash_error(_('No such user: %s') % id)

            if user_obj:
                try:
                    mailer.send_reset_link(user_obj)
                    return render('user/ote_close.html')
                except mailer.MailerException, e:
                    h.flash_error(_('Could not send reset link: %s') %
                                  unicode(e))
        return render('user/request_reset.html')

    def login(self, error=None):
        # Do any plugin login stuff
        for item in p.PluginImplementations(p.IAuthenticator):
            item.login()

        if 'error' in request.params:
            h.flash_error(request.params['error'])

        if not c.user:
            came_from = request.params.get('came_from')
            if not came_from:
                came_from = h.url_for(controller='user', action='logged_in',
                                      __ckan_no_root=True)
            c.login_handler = h.url_for(
                self._get_repoze_handler('login_handler_path'),
                came_from=came_from)
            if error:
                vars = {'error_summary': {'': error}}
            else:
                vars = {}
            return render('user/login.html', extra_vars=vars)
        else:
            return render('user/logout_first.html')

    def logged_in(self):
        # redirect if needed
        came_from = request.params.get('came_from', '')
        if h.url_is_local(came_from):
            return h.redirect_to(str(came_from))

        if c.user:
            context = None
            data_dict = {'id': c.user}

            user_dict = get_action('user_show')(context, data_dict)

            return h.redirect_to(str('/ote/#/?logged_in=1'))
        else:
            err = _('Login failed. Bad username or password.')
            if asbool(config.get('ckan.legacy_templates', 'false')):
                h.flash_error(err)
                h.redirect_to(controller='user',
                              action='login', came_from=came_from)
            else:
                return self.login(error=err)

    def logout(self):
        # Do any plugin logout stuff
        for item in p.PluginImplementations(p.IAuthenticator):
            item.logout()
        url = h.url_for(controller='home', action='index',
                        __ckan_no_root=True)
        h.redirect_to(self._get_repoze_handler('logout_handler_path') +
                      '?came_from=' + url)

    def edit(self, id=None, data=None, errors=None, error_summary=None):
        return super(CustomUserController, self).edit(id, data, errors, error_summary)

    def _save_edit(self, id, context):
        try:
            if id in (c.userobj.id, c.userobj.name):
                current_user = True
            else:
                current_user = False
            old_username = c.userobj.name

            data_dict = logic.clean_dict(unflatten(
                logic.tuplize_dict(logic.parse_params(request.params))))
            context['message'] = data_dict.get('log_message', '')
            data_dict['id'] = id

            email_changed = data_dict['email'] != c.userobj.email
            if (data_dict['password1'] and data_dict['password2']) \
                    or email_changed:
                identity = {'login': c.user,
                            'password': data_dict['old_password']}
                auth = authenticator.UsernamePasswordAuthenticator()

                if auth.authenticate(request.environ, identity) != c.user:
                    raise UsernamePasswordError

            # MOAN: Do I really have to do this here?
            if 'activity_streams_email_notifications' not in data_dict:
                data_dict['activity_streams_email_notifications'] = False

            user = get_action('user_update')(context, data_dict)

            h.flash_success(_('Profile updated'))

            if current_user and data_dict['name'] != old_username:
                # Changing currently logged in user's name.
                # Update repoze.who cookie to match
                set_repoze_user(data_dict['name'])
            return render('user/ote_close.html')
        except NotAuthorized:
            abort(403, _('Unauthorized to edit user %s') % id)
        except NotFound, e:
            abort(404, _('User not found'))
        except DataError:
            abort(400, _(u'Integrity Error'))
        except ValidationError, e:
            errors = e.error_dict
            error_summary = e.error_summary
            return self.edit(id, data_dict, errors, error_summary)
        except UsernamePasswordError:
            errors = {'oldpassword': [_('Password entered was incorrect')]}
            error_summary = {_('Old Password'): _('incorrect password')}
            return self.edit(id, data_dict, errors, error_summary)
