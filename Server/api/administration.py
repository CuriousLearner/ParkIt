from flask.ext.admin.contrib.mongoengine import ModelView, filters, EmbeddedForm
from flask.ext.admin import Admin, BaseView, expose
from flask_admin.form import rules
from flask.ext.security import current_user, login_required
import api
from wtforms.validators import required, ValidationError
from datetime import datetime


# class VehicleEmbeddedView(EmbeddedForm):
#     form_column = (vid,)
#     form_widget_args = {'vid': {'disabled': True}}


# class TrasactionEmbeddedView(EmbeddedForm):
#     form_widget_args = {''}

class TransactionView(ModelView):
    can_create = False
    can_delete = True
    can_edit = False
    column_list = ('QR_CODE_DATA', 'cost', 'total_cost', 'active', 'entry_time_stamp', 'exit_time_stamp')
    decorators = [login_required]

    # form_widget_args = {'cost': {'disabled': True}, 
    #                     'total_cost': {'disabled': True}, 
    #                     'active': {'disabled': True},
    #                     'entry_time_stamp': {'disabled': True},
    #                     'exit_time_stamp': {'disabled': True}}

    def is_accessible(self):
        return current_user.has_role("admin") or current_user.has_role("super_admin")

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
                        'QR_CODE_DATA': {'disabled': True},
                        'latest_transaction_cost': {'disabled': True},
                        'driving_licence_link': {'disabled': True}}
    column_filters = ('cid', 'driving_licence_link', 'first_name', 'last_name')
    # form_subdocuments = {
    #                 'vehicles': {
    #                     'form_subdocuments': {
    #                         None: {
    #                             'form_columns': ('vehicle_type',),
    #                             # 'form_widget_args': {'vid': {'disabled': True}}
    #                         }
    #                     }
    #                 }
    #             }

    def is_accessible(self):
        return current_user.has_role("admin") or current_user.has_role("super_admin")


class VehicleView(ModelView):
    can_create = True
    can_delete = True
    can_edit = True
    column_list = ('vid', 'vehicle_type', 'vehicle_number')
    decorators = [login_required]
    form_widget_args = {'vid': {'disabled': True}}

    column_filters = ( 'vehicle_number','vehicle_type')

    def is_accessible(self):
        return current_user.has_role("admin") or current_user.has_role("super_admin")


class CostView(ModelView):
    can_create = True
    can_delete = True
    can_edit = True
    column_list = ('parking_lot_name', 'two_wheeler', 'four_wheeler', 'heavy_vehicle')
    decorators = [login_required]

    column_filters = ('parking_lot_name',)

    def is_accessible(self):
        return current_user.has_role("admin") or current_user.has_role("super_admin")


class UserView(ModelView):
    can_create = True
    can_delete = True
    can_edit = True
    decorators = [login_required]

    column_filters = ('email',)

    def is_accessible(self):
        return current_user.has_role("super_admin")

class RoleView(ModelView):
    can_create = True
    can_delete = True
    can_edit = True
    decorators = [login_required]

    def is_accessible(self):
        return current_user.has_role("super_admin")

api.admin.add_view(CustomerView(api.models.Customer))
api.admin.add_view(VehicleView(api.models.Vehicle))
api.admin.add_view(CostView(api.models.Cost))
api.admin.add_view(TransactionView(api.models.Transaction))
api.admin.add_view(UserView(api.models.User))
api.admin.add_view(RoleView(api.models.Role))
