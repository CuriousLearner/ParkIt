from flask.ext.admin.contrib.mongoengine import ModelView, filters
from flask.ext.admin import Admin, BaseView, expose
from flask_admin.form import rules
from flask.ext.security import current_user, login_required
import api
from wtforms.validators import required, ValidationError
from datetime import datetime


class CustomerView(ModelView):
    can_create = True
    can_delete = True
    can_edit = True
    column_list = ('cid', 'first_name', 'last_name', 'contact_no', 
                    'address', 'created_on', 'modified_on', 'QR_CODE_DATA')
    decorators = [login_required]
    form_widget_args = {'cid': {'disabled': True}, 
                        'created_on': {'disabled': True}, 
                        'modified_on': {'disabled': True},
                        'transactions' : {'disabled': True}}

    def is_accessible(self):
        return current_user.has_role("admin") or current_user.has_role("super_admin")


class VehicleView(ModelView):
    can_create = True
    can_delete = True
    can_edit = True
    column_list = ('vid', 'vehicle_type')
    decorators = [login_required]
    form_widget_args = {'vid': {'disabled': True}}

    def is_accessible(self):
        return current_user.has_role("admin") or current_user.has_role("super_admin")


class CostView(ModelView):
    can_create = True
    can_delete = True
    can_edit = True
    column_list = ('two_wheeler', 'four_wheeler', 'heavy_vehicle')
    decorators = [login_required]

    def is_accessible(self):
        return current_user.has_role("admin") or current_user.has_role("super_admin")


class UserView(ModelView):
    can_create = True
    can_delete = True
    can_edit = True
    decorators = [login_required]

    def is_accessible(self):
        return current_user.has_role("super_admin")

api.admin.add_view(CustomerView(api.models.Customer))
api.admin.add_view(VehicleView(api.models.Vehicle))
api.admin.add_view(CostView(api.models.Cost))
api.admin.add_view(UserView(api.models.User))
# api.admin.add_view(TransactionView(api.models.Transaction))
