# encoding: utf-8

import logging
log = logging.getLogger(__name__)

import ckan.lib.base as base
import ckan.lib.helpers as h
import ckan.lib.navl.dictization_functions as dict_fns
import ckan.logic as logic
import ckan.model as model
import ckan.authz as authz
import ckan.lib.plugins
from ckan.common import OrderedDict, c, config, request, _

log = logging.getLogger(__name__)

render = base.render
abort = base.abort

NotFound = logic.NotFound
NotAuthorized = logic.NotAuthorized
ValidationError = logic.ValidationError
check_access = logic.check_access
get_action = logic.get_action
tuplize_dict = logic.tuplize_dict
clean_dict = logic.clean_dict
parse_params = logic.parse_params

lookup_group_plugin = ckan.lib.plugins.lookup_group_plugin
lookup_group_controller = ckan.lib.plugins.lookup_group_controller

import ckan.controllers.group as group
import ckan.plugins as plugins
from ckan.controllers.organization import OrganizationController

class CustomOrganizationController(OrganizationController):

    def _save_new(self, context, group_type=None):
        try:
            data_dict = clean_dict(dict_fns.unflatten(
                tuplize_dict(parse_params(request.params))))
            data_dict['type'] = group_type or 'group'
            context['message'] = data_dict.get('log_message', '')
            data_dict['users'] = [{'name': c.user, 'capacity': 'admin'}]
            group = self._action('group_create')(context, data_dict)

            # Redirect to the appropriate _read route for the type of group
            if group['type'] == 'organization':
                h.redirect_to(h.url_for_static('organization/edit/{0}'.format(group['name'])))
            else:
                h.redirect_to(group['type'] + '_read', id=group['name'])
        except (NotFound, NotAuthorized), e:
            abort(404, _('Group not found'))
        except dict_fns.DataError:
            abort(400, _(u'Integrity Error'))
        except ValidationError, e:
            errors = e.error_dict
            error_summary = e.error_summary
            return self.new(data_dict, errors, error_summary)

    def member_new(self, id):
        group_type = self._ensure_controller_matches_group_type(id)

        context = {'model': model, 'session': model.Session,
                   'user': c.user}
        try:
            self._check_access('group_member_create', context, {'id': id})
        except NotAuthorized:
            abort(403, _('Unauthorized to create group %s members') % '')

        try:
            data_dict = {'id': id}
            data_dict['include_datasets'] = False
            c.group_dict = self._action('group_show')(context, data_dict)
            c.roles = self._action('member_roles_list')(
                context, {'group_type': group_type}
            )

            if request.method == 'POST':
                data_dict = clean_dict(dict_fns.unflatten(
                    tuplize_dict(parse_params(request.params))))
                data_dict['id'] = id

                email = data_dict.get('email')

                if email:
                    ## CUSTOMIZATION ##
                    # Check if email is not already used be some user before sending invite and creating a placeholder
                    # user account
                    users = model.User.by_email(email)

                    if users:
                        msg = _('Email already exists')

                        raise ValidationError({'message': msg},
                                              error_summary=msg)

                    ## CUSTOMIZATION END ##

                    user_data_dict = {
                        'email': email,
                        'group_id': data_dict['id'],
                        'role': data_dict['role']
                    }
                    del data_dict['email']
                    user_dict = self._action('user_invite')(
                        context, user_data_dict)
                    data_dict['username'] = user_dict['name']

                c.group_dict = self._action('group_member_create')(
                    context, data_dict)

                self._redirect_to_this_controller(action='members', id=id)
            else:
                user = request.params.get('user')
                if user:
                    c.user_dict = \
                        get_action('user_show')(context, {'id': user})
                    c.user_role = \
                        authz.users_role_for_group_or_org(id, user) or 'member'
                else:
                    c.user_role = 'member'
        except NotAuthorized:
            abort(403, _('Unauthorized to add member to group %s') % '')
        except NotFound:
            abort(404, _('Group not found'))
        except ValidationError, e:
            log.info(e)
            h.flash_error(e.error_summary)
        return self._render_template('group/member_new.html', group_type)