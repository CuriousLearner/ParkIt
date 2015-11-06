from api import app, db
from flask import (render_template, jsonify, request, redirect, url_for,
                   Response, make_response)
from models import Customer, Vehicle
from flask.ext.security import login_required, utils
import json
from datetime import datetime
from os import environ
import requests
from flask.ext.security.utils import encrypt_password

result = []  # Global result list to return result as JSON

@app.route('/api/customer/register', methods=['GET', 'POST'])
@app.route('/api/customer/register/', methods=['GET', 'POST'])
def register_customer():
    if request.method == 'POST':
        # return Response(json.dumps({"status": "True"}), status=200)
        json_response = json.loads(request.data)
        c = Customer()
        c.first_name = json_response['first_name']
        c.last_name = json_response['last_name']
        c.contact_no = json_response['contact_no']
        c.address = json_response['address']
        c.save()
        # c = Customer.objects.all().limit(1)
        return Response(json.dumps({"QR_CODE_DATA": c.QR_CODE_DATA}), status=200,
                            content_type="application/json")

@app.route('/api/customer/')
def get_customer():
    '''
    returns all the events with GET request
    '''
    allCustomer = Customer.objects.all()
    create_dict(allCustomer)
    return Response(json.dumps(result, cls=PythonJSONEncoder), status=200,
                    content_type="application/json")


@app.route('/api/customer/cid/<int:cid>')
def get_single_customer(cid):
    '''
    returns all the events with GET request
    '''
    allCustomer = Customer.objects.filter(cid=cid)
    x = create_dict(allCustomer)
    return Response(json.dumps(x, cls=PythonJSONEncoder), status=200,
                    content_type="application/json")


# Test QR for cid 5 "$5$rounds=110000$wvPmSMcQcdbYGTnb$w86ZYahOCG8Vo7NFD4ZiVJDZQGs9fzmLJbiVAtOIiK8"

@app.route('/api/customer/qrcode', methods=['GET', 'POST'])
@app.route('/api/customer/qrcode/', methods=['GET', 'POST'])
def get_customer_for_qr():
    '''
    returns all the events with GET request
    '''
    if request.method == 'POST':
        json_response = json.loads(request.data)
        QR_CODE_DATA = json_response['QR_CODE_DATA']
        allCustomer = Customer.objects.filter(QR_CODE_DATA=QR_CODE_DATA)
        x = create_dict(allCustomer)
        return Response(json.dumps(x, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")

@app.route('/api/vehicles/<int:vehicle_id>')
def get_single_vehicle(vehicle_id):
    singleVehicle = Vehicle.objects(vid=vehicle_id)
    result = create_dict(singleVehicle)
    return Response(json.dumps(result, cls=PythonJSONEncoder), status=200,
                    content_type="application/json")


@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({"Error": "Not found"}), 404)


class PythonJSONEncoder(json.JSONEncoder):

    """
    Custom JSON Encoder to encode unsupported data-types to pythonic
    representations.
    """

    def default(self, obj):
        if isinstance(obj, Customer):
            return obj.get_dict()
        if isinstance(obj, Vehicle):
            return obj.get_dict()
        elif isinstance(obj, datetime):
            return obj.isoformat()
        elif isinstance(obj, datetime.date):
            return obj.isoformat()
        elif isinstance(obj, datetime.time):
            return obj.isoformat()
        else:
            return repr(obj)
        return super(PythonJSONEncoder, self).default(obj)


def unjsonify(dct):
    if 'eid' in dct:
        dct['eid'] = round(eval(dct['eid']), 1)
    return dct


def create_dict(allCustomer):
    global result  # To store the result of all Customer
    result = []  # Empty for each call
    for item in allCustomer:
        d = {}  # To make a dictionary for JSON Response
        d['cid'] = item.cid
        d['first_name'] = item.first_name
        d['last_name'] = item.last_name
        d['contact_no'] = item.contact_no
        d['address'] = item.address
        result.append(d)
    # For single Customer
    if len(result) == 1:
        for item in result:
            return item
    # Else return the whole list of Customers
    return result
