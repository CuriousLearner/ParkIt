from api import db
from flask_security import Security, UserMixin, \
    RoleMixin, login_required, current_user
from datetime import datetime
from flask.ext.security.utils import encrypt_password
import hashlib

VEHICLES = [
    'two_wheeler',
    'four_wheeler',
    'heavy_vehicle'
]


class Cost(db.Document):
    parking_lot_name = db.StringField(max_length=100)
    two_wheeler = db.IntField()
    four_wheeler = db.IntField()
    heavy_vehicle = db.IntField()

    def __str__(self):
        return "Parking: {} two_wheeler :{}, four_wheeler: {}, heavy_vehicle: {}".format(self.parking_lot_name, self.two_wheeler, self.four_wheeler, self.heavy_vehicle)


class Transaction(db.Document):
    cost = db.ReferenceField(Cost, dbref=False)
    QR_CODE_DATA = db.StringField()
    total_cost = db.IntField()
    entry_time_stamp = db.DateTimeField()
    exit_time_stamp = db.DateTimeField()
    active = db.BooleanField(default=False)

    def __str__(self):
        return "total_cost: {}, cost: {}, entry: {}, exit: {}".format(self.total_cost, self.cost, self.entry_time_stamp, self.exit_time_stamp)

    # def save(self, *args, **kwargs):
    #     pass


class Vehicle(db.Document):
    vid = db.IntField(unique=True)
    vehicle_type = db.StringField(choices=VEHICLES)
    vehicle_number = db.StringField()

    def __unicode__(self):
        return str(self.vehicle_type) + ": " + str(self.vehicle_number)

    def get_dict(self):
        return {
                'vid': self.vid,
                'vehicle_type': self.vehicle_type,
                'vehicle_number': self.vehicle_number
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
    # documents = ['doc1', 'doc2']
    # vehicle_id = [vid1, vid2]
    # Last Transaction = { 'cost': '', 'time': '', 'date':''}
    QR_CODE_DATA = db.StringField(max_length=200)
    vehicles = db.ListField(db.ReferenceField(Vehicle, dbref=False), default=[])
    # transactions = db.ListField(db.EmbeddedDocumentField(Transaction))
    transactions = db.ListField(db.ReferenceField(Transaction, dbref=False))


    def __unicode__(self):
        return str(self.cid)

    def get_dict(self):
        return {'cid': self.cid,
                'first_name': self.first_name,
                'last_name': self.last_name,
                'contact_no': self.contact_no,
                'address': self.address
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


# class Documents(db.Documents):
#     user_id_card = db.UrlField()
#     # vehicle_docs = 


# class Vehicle_Docs(db.Document):
#     vehicle_rc = db.UrlField() 


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
