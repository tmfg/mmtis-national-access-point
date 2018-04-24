# encoding: utf-8

import logging

from ckan.logic.validators import email_validator
import ckan.lib.navl.dictization_functions as df
from ckan.common import _

log = logging.getLogger(__name__)

Invalid = df.Invalid

def email_uniq_validator(value, context):
    model = context['model']
    username = context['user']
    user_obj = context.get('user_obj')

    if user_obj == None or (username == user_obj.name and user_obj.email != value):
        # Check if editing self and email has changed or registering new user
        users = model.User.by_email(value)

        if users:
            raise Invalid(_('Email already exists'))

    return email_validator(value, context)
