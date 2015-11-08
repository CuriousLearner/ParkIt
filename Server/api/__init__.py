from flask import Flask
from flask.ext.admin import Admin
from flask.ext.mongoengine import MongoEngine
from pymongo import read_preferences
from flask.ext.security import MongoEngineUserDatastore, Security
from os import environ
from flask.ext.security.utils import encrypt_password
from flask.ext.cors import CORS

app = Flask(__name__, instance_relative_config=True)
app.config.from_object('config')

cors = CORS(app, resources={r"/api/*": {"origins": "*"}})

db = MongoEngine()
admin = Admin(name="ParkIt API")
# app.secret_key = environ['SECRET_KEY']
app.secret_key = 'SECRET_KEY'

# Configurations for MongoDB
# app.config['MONGODB_SETTINGS'] = {'DB': environ['DB'], 'HOST': environ['HOST']}
app.config['MONGODB_SETTINGS'] = {'DB': 'parkit', 'HOST': 'localhost'}

# Configurations for storing password hashes
# app.config['SECURITY_PASSWORD_HASH'] = environ['SECURITY_PASSWORD_HASH']
# app.config['SECURITY_PASSWORD_SALT'] = environ['SECURITY_PASSWORD_SALT']
app.config['SECURITY_PASSWORD_HASH'] = 'sha256_crypt'
app.config['SECURITY_PASSWORD_SALT'] = 'SECURITY_PASSWORD_SALT'

# app.config['SECURITY_POST_LOGIN_VIEW'] = environ['SECURITY_POST_LOGIN_VIEW']
# app.config['SECURITY_POST_LOGIN_VIEW'] = environ['SECURITY_POST_LOGIN_VIEW']
app.config['SECURITY_POST_LOGIN_VIEW'] = '/admin/customer/'

db.init_app(app)
admin.init_app(app)

from api import models
from api import views
from api import administration

# Setup Flask-Security
user_datastore = MongoEngineUserDatastore(db, models.User, models.Role)
security = Security(app, user_datastore)

# sanyam = models.User(email="Sanyam.Khurana@TheGeekyWay.com", password="hello")
@app.before_first_request
def before_first_request():
    user_datastore.find_or_create_role(name='admin', description='Administrator')
    user_datastore.find_or_create_role(name='super_admin', description='Super Administrator')
    encrypted_password = encrypt_password('password')
    if not user_datastore.get_user('sanyam.khurana@thegeekyway.com'):
        user_datastore.create_user(email='sanyam.khurana@thegeekyway.com', password=encrypted_password)
        user_datastore.add_role_to_user('sanyam.khurana@thegeekyway.com', 'admin')
        user_datastore.add_role_to_user('sanyam.khurana@thegeekyway.com', 'super_admin')
