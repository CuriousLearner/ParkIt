from api import db
from flask_security import Security, UserMixin, \
    RoleMixin, login_required, current_user
from datetime import datetime
from flask.ext.security.utils import encrypt_password
import hashlib

VEHICLES = (
    'two_wheeler',
    'four_wheeler',
    'heavy_vehicle'
)


class Cost(db.Document):
    parking_lot_name = db.StringField()
    two_wheeler = db.IntField()
    four_wheeler = db.IntField()
    heavy_vehicle = db.IntField()

    def __str__(self):
        return "COST FOR two_wheeler :{}, four_wheeler: {}, heavy_vehicle: {}".format(self.two_wheeler, self.four_wheeler, self.heavy_vehicle)

    def get_dict(self):
        return {
                'parking_lot_name': self.parking_lot_name,
                'two_wheeler': self.two_wheeler,
                'four_wheeler': self.four_wheeler,
                'heavy_vehicle': self.heavy_vehicle
                }


class Transaction(db.Document):
    cost = db.ReferenceField(Cost, dbref=False)
    parking_lot_name = db.StringField()
    QR_CODE_DATA = db.StringField()
    total_cost = db.IntField()
    entry_time_stamp = db.DateTimeField()
    exit_time_stamp = db.DateTimeField()
    active = db.BooleanField(default=False)

    def __str__(self):
        return "total_cost: {}, cost: {}, entry: {}, exit: {}".format(self.total_cost, self.cost, self.entry_time_stamp, self.exit_time_stamp)

    def get_dict(self):
        return {
                'total_cost': self.total_cost,
                'entry_time_stamp': self.entry_time_stamp,
                'exit_time_stamp': self.exit_time_stamp,
                'parking_lot_name': self.parking_lot_name
                }


class ParkingLot(db.Document):
    parking_lot_name = db.StringField(max_length=100)
    cost = db.ReferenceField(Cost, dbref=False)
    two_wheeler_capacity = db.IntField()
    four_wheeler_capacity = db.IntField()
    heavy_vehicle_capacity = db.IntField()
    current_two_wheeler = db.IntField(default=0)
    current_four_wheeler = db.IntField(default=0)
    current_heavy_vehicle = db.IntField(default=0)
    transactions = db.ListField(db.ReferenceField(Transaction, dbref=False))

    def __str__(self):
        return "Parking: {} two_wheeler :{}, four_wheeler: {}, heavy_vehicle: {}".format(self.parking_lot_name, self.cost.two_wheeler, self.cost.four_wheeler, self.cost.heavy_vehicle)

    def get_dict(self):
        return {
                'parking_lot_name': self.parking_lot_name,
                'transactions': self.transactions
                }


class Vehicle(db.Document):
    vid = db.IntField(unique=True)
    vehicle_type = db.StringField(choices=VEHICLES)
    vehicle_number = db.StringField()
    vehicle_rc_link = db.URLField()

    def __unicode__(self):
        return str(self.vehicle_type) + ": " + str(self.vehicle_number)

    def get_dict(self):
        return {
                'vid': self.vid,
                'vehicle_type': self.vehicle_type,
                'vehicle_number': self.vehicle_number,
                'vehicle_rc_link': self.vehicle_rc_link
                }

    def __repr__(self):
        return 'vehicle_type ' + str(self.vehicle_type) + 'vehicle_number ' + str(self.vehicle_number)

    def save(self, *args, **kwargs):
        if self.vid == None:
            try:
                self.vid = self.__class__.objects.order_by('-vid')[0].vid + 1
            except IndexError:
                self.vid = Vehicle.objects.count() + 1
        super(Vehicle, self).save(*args, **kwargs)


class Customer(db.Document):
    cid = db.IntField(unique=True)
    first_name = db.StringField(max_length=100)
    last_name = db.StringField(max_length=100)
    contact_no = db.IntField(min_value=1000000000, max_value=9999999999)
    address = db.StringField(max_length=500)
    created_on = db.DateTimeField()
    modified_on = db.DateTimeField()
    latest_transaction_cost = db.IntField()
    driving_licence_link = db.URLField()
    QR_CODE_DATA = db.StringField(max_length=200)
    vehicles = db.ListField(db.ReferenceField(Vehicle, dbref=False), default=[])
    transactions = db.ListField(db.ReferenceField(Transaction, dbref=False))


    def __unicode__(self):
        return str(self.cid)

    def get_dict(self):
        return {'cid': self.cid,
                'first_name': self.first_name,
                'last_name': self.last_name,
                'contact_no': self.contact_no,
                'address': self.address,
                'latest_transaction_cost': latest_transaction_cost,
                'driving_licence_link':self.driving_licence_link
                }

    def __repr__(self):
        return 'cid ' + str(self.cid)

    def save(self, *args, **kwargs):
        if self.cid == None:
            try:
                self.cid = self.__class__.objects.order_by('-cid')[0].cid + 1
            except IndexError:
                self.cid = Customer.objects.count() + 1
        if not self.created_on:
            self.created_on = datetime.now()
        self.modified_on = datetime.now()
        if not self.QR_CODE_DATA:
            self.QR_CODE_DATA = hashlib.sha1(str(self.cid) + str(self.created_on)).hexdigest()
        super(Customer, self).save(*args, **kwargs)


class Role(db.Document, RoleMixin):
    name = db.StringField(max_length=80, unique=True)
    description = db.StringField(max_length=255)

    def __unicode__(self):
        return self.name

    def __repr__(self):
        return self.name


class User(db.Document, UserMixin):
    email = db.StringField(max_length=255)
    password = db.StringField(max_length=500)
    active = db.BooleanField(default=True)
    confirmed_at = db.DateTimeField()
    roles = db.ListField(db.ReferenceField(Role), default=[])

    def save(self, *args, **kwargs):
        self.password = encrypt_password(self.password)
        self.confirmed_at = datetime.now()

        super(User, self).save(*args, **kwargs)
