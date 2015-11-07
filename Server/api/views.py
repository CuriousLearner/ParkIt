from api import app, db
from flask import (render_template, jsonify, request, redirect, url_for,
                   Response, make_response)
from models import Customer, Vehicle, Transaction, Cost
from flask.ext.security import login_required, utils
import json
from datetime import datetime
from os import environ
import requests
from flask.ext.security.utils import encrypt_password
from math import ceil

result = []  # Global result list to return result as JSON

@app.route('/api/customer/register', methods=['GET', 'POST'])
@app.route('/api/customer/register/', methods=['GET', 'POST'])
def register_customer():
    if request.method == 'POST':
        # return Response(json.dumps({"status": "True"}), status=200)
        json_request = json.loads(request.data)
        c = Customer()
        c.first_name = json_request['first_name']
        c.last_name = json_request['last_name']
        c.contact_no = json_request['contact_no']
        c.address = json_request['address']
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
    SingleCustomer = Customer.objects.get(cid=cid)
    x = create_single_customer_dict(SingleCustomer)
    return Response(json.dumps(x, cls=PythonJSONEncoder), status=200,
                    content_type="application/json")


# Test QR for cid 5 "$5$rounds=110000$wvPmSMcQcdbYGTnb$w86ZYahOCG8Vo7NFD4ZiVJDZQGs9fzmLJbiVAtOIiK8"

@app.route('/api/customer/entry', methods=['GET', 'POST'])
@app.route('/api/customer/entry/', methods=['GET', 'POST'])
def get_customer_from_qr_and_enter_parking():
    '''
    returns all the events with GET request
    '''
    if request.method == 'POST':
        json_request = json.loads(request.data)
        # vehicle_type = json_request['vehicle_type']
        parking_lot_name = json_request['parking_lot_name']
        QR_CODE_DATA = json_request['QR_CODE_DATA']
        SingleCustomer = Customer.objects.get(QR_CODE_DATA=QR_CODE_DATA)
        x = create_single_customer_dict(SingleCustomer)
        cost_obj = Cost.objects.get(parking_lot_name=parking_lot_name)
        t = Transaction()
        t.QR_CODE_DATA = QR_CODE_DATA
        t.entry_time_stamp = datetime.now()
        t.cost = cost_obj
        t.save()
        print('t : ' + str(t))
        return Response(json.dumps(x, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")


@app.route('/api/customer/exit', methods=['GET', 'POST'])
@app.route('/api/customer/exit/', methods=['GET', 'POST'])
def make_transaction():
    if request.method == 'POST':
        json_request = json.loads(request.data)
        QR_CODE_DATA = json_request['QR_CODE_DATA']
        vehicle_type = json_request['vehicle_type']
        parking_lot_name = json_request['parking_lot_name']
        c = Customer.objects.get(QR_CODE_DATA=QR_CODE_DATA)
        # print(c.transactions)
        transactions = Transaction.objects.filter(QR_CODE_DATA=QR_CODE_DATA)
        cost_obj = Cost.objects.get(parking_lot_name=parking_lot_name)
        # print('cost ' + str(cost_obj))
        for transaction in transactions:
            # print(type(transaction))
            if not transaction.exit_time_stamp:
                transaction['exit_time_stamp'] = datetime.now()
                transaction.cost = cost_obj
                td = transaction['exit_time_stamp'] - transaction['entry_time_stamp']
                total_time = (td.days * 24) + ceil(td.seconds/3600)
                # print(td.seconds//60, (td.seconds//60)%60)
                # print('total_time' + str(total_time))
                # print('transaction ' + str(transaction))
                # transaction.total_cost = 0
                if vehicle_type == 'two_wheeler':
                    transaction.total_cost = transaction.cost.two_wheeler * total_time
                elif vehicle_type == 'four_wheeler':
                    transaction.total_cost = transaction.cost.four_wheeler * total_time
                elif vehicle_type == 'heavy_vehicles':
                    transaction.total_cost = transaction.cost.heavy_vehicles * total_time
                print(transaction.total_cost)
                transaction.save()
                c.transactions.append(transaction)
                # print('transaction ' + str(transaction))
                # print(type(transaction))
                c.save()
        return Response(json.dumps({"cost": transaction.total_cost}, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")


@app.route('/api/customer/transaction/cost', methods=['GET', 'POST'])
@app.route('/api/customer/transaction/cost/', methods=['GET', 'POST'])
def get_latest_transaction_cost():
    if request.method == 'POST':
        json_request = json.loads(request.data)
        QR_CODE_DATA = json_request['QR_CODE_DATA']
        transaction = Transaction.objects.filter(QR_CODE_DATA=QR_CODE_DATA).limit(1)
        # t = create_transaction_dict(transaction)
        print(transaction._get_as_pymongo())
        return Response(json.dumps({"cost": t.total_cost}, cls=PythonJSONEncoder), status=200,
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
    return result

def create_single_customer_dict(SingleCustomer):
    d = {}
    d['cid'] = SingleCustomer.cid
    d['first_name'] = SingleCustomer.first_name
    d['last_name'] = SingleCustomer.last_name
    d['contact_no'] = SingleCustomer.contact_no
    d['address'] = SingleCustomer.address
    d['vehicles'] = SingleCustomer.vehicles
    return d

def create_transaction_dict(SingleTransaction):
    d = {}
    print(dir(SingleTransaction))
    d['cost'] = SingleTransaction.cost
    return d
