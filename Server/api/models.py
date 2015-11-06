from api import db
from flask_security import Security, UserMixin, \
    RoleMixin, login_required, current_user
from datetime import datetime
from flask.ext.security.utils import encrypt_password


class Cost(db.Document):
    two_wheeler = db.IntField()
    four_wheeler = db.IntField()
    heavy_vehicle = db.IntField()

    def __str__(self):
        return "two_wheeler :{}, four_wheeler: {}, heavy_vehicle: {}".format(self.two_wheeler, self.four_wheeler, self.heavy_vehicle)


class Transaction(db.EmbeddedDocument):
    cost = db.ReferenceField(Cost, dbref=False)
    total_cost = db.IntField()
    entry_time_stamp = db.DateTimeField()
    exit_time_stamp = db.DateTimeField()

    def __str__(self):
        return "cost: {}, entry: {}, exit: {}".format(self.cost, self.entry_time_stamp, self.exit_time_stamp)

    def save(self, *args, **kwargs):
        if not entry_time_stamp:
            entry_time_stamp = datetime.now()


class Customer(db.Document):
    cid = db.IntField(unique=True)
    first_name = db.StringField(max_length=100)
    last_name = db.StringField(max_length=100)
    contact_no = db.IntField()
    address = db.StringField(max_length=500)
    created_on = db.DateTimeField()
    modified_on = db.DateTimeField()
    # documents = ['doc1', 'doc2']
    # vehicle_id = [vid1, vid2]
    # Last Transaction = { 'cost': '', 'time': '', 'date':''}
    QR_CODE_DATA = db.StringField(max_length=200)
    transactions = db.ListField(db.EmbeddedDocumentField(Transaction))

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
        self.QR_CODE_DATA = encrypt_password(str(self.created_on))
        super(Customer, self).save(*args, **kwargs)


class Vehicle(db.Document):
    vid = db.IntField(unique=True)
    vehicle_type = db.StringField(max_length=100)

    def __unicode__(self):
        return str(self.vid)

    def get_dict(self):
        return {'vid': self.vid,
                'vehicle_type': self.vehicle_type,
                }

    def __repr__(self):
        return 'vid ' + str(self.vid)

    def save(self, *args, **kwargs):
        if self.vid == None:
            try:
                self.vid = self.__class__.objects.order_by('-vid')[0].vid + 1
            except IndexError:
                self.vid = Vehicle.objects.count() + 1

        super(Vehicle, self).save(*args, **kwargs)


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
