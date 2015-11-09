from api import app, db
from flask import (render_template, jsonify, request, redirect, url_for,
                   Response, make_response)
from models import Customer, Vehicle, Transaction, ParkingLot, Cost
from flask.ext.security import login_required, utils
import json
from datetime import datetime
from os import environ
import requests
from flask.ext.security.utils import encrypt_password
from math import ceil

@app.route('/api/customer/register', methods=['GET', 'POST'])
@app.route('/api/customer/register/', methods=['GET', 'POST'])
def register_customer():
    if request.method == 'POST':
        json_request = json.loads(request.data)
        try:
            if not check_auth(json_request['token']):
                return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                            content_type="application/json")
        except KeyError:
            return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                            content_type="application/json")
        c = Customer()
        try:
            c.first_name = json_request['first_name']
            c.last_name = json_request['last_name']
            c.address = json_request['address']
            c.contact_no = json_request['contact_no']
            c.driving_licence_link = json_request['driving_licence_link']
            vehicles = json_request['vehicles']
        except:
            return Response(json.dumps({"Message": "In-Complete form data"}), status=400,
                                content_type="application/json")
        try:
            if not(int(c.contact_no) > 1000000000 and int(c.contact_no) <= 9999999999):
                return Response(json.dumps({"Message": "Contact no. not valid"}), status=400,
                                content_type="application/json")
        except:
            return Response(json.dumps({"Message": "Contact no. not valid"}), status=400,
                            content_type="application/json")
        for vehicle in vehicles:
            v = Vehicle()
            try:
                v.vehicle_type = vehicle['vehicle_type']
                v.vehicle_number = vehicle['vehicle_number']
                v.vehicle_rc_link = vehicle['vehicle_rc_link']
            except:
                return Response(json.dumps({"Message": "In-Complete form data"}), status=400,
                                content_type="application/json")
            v.save()
            c.vehicles.append(v)
        c.save()
        return Response(json.dumps({"QR_CODE_DATA": c.QR_CODE_DATA}), status=200,
                            content_type="application/json")

@app.route('/api/customer/', methods=['GET', 'POST'])
def get_customer():
    '''
    returns all the events with GET request
    '''
    if request.method == 'POST':
        json_request = json.loads(request.data)
        try:
            if not check_auth(json_request['token']):
                return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                            content_type="application/json")
        except KeyError:
            return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                            content_type="application/json")
        allCustomer = Customer.objects.all()
        create_dict(allCustomer)
        return Response(json.dumps(result, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")


@app.route('/api/customer/cid/<int:cid>', methods=['GET', 'POST'])
@app.route('/api/customer/cid/<int:cid>/', methods=['GET', 'POST'])
def get_single_customer(cid):
    '''
    returns all the events with GET request
    '''
    if request.method == 'POST':
        json_request = json.loads(request.data)
        try:
            if not check_auth(json_request['token']):
                return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                            content_type="application/json")
        except KeyError:
            return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                            content_type="application/json")
        try:
            SingleCustomer = Customer.objects.get(cid=cid)
        except:
            return Response(json.dumps({"Message": "No Customer found"}), status=404,
                            content_type="application/json")
        x = create_single_customer_dict(SingleCustomer)
        return Response(json.dumps(x, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")


@app.route('/api/customer/entry', methods=['GET', 'POST'])
@app.route('/api/customer/entry/', methods=['GET', 'POST'])
def get_customer_from_qr_and_enter_parking():
    '''
    returns all the events with GET request
    '''
    if request.method == 'POST':
        json_request = json.loads(request.data)
        try:
            if not check_auth(json_request['token']):
                return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                            content_type="application/json")
        except KeyError:
            return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                            content_type="application/json")
        try:
            parking_lot_name = json_request['parking_lot_name']
            QR_CODE_DATA = json_request['QR_CODE_DATA']
            vehicle_type = json_request['vehicle_type']
        except:
            return Response(json.dumps({"Message": "parking_lot_name or QR_CODE_DATA or vehicle_type not present"}), status=400,
                                content_type="application/json")
        # Check if customer is there
        try:
            SingleCustomer = Customer.objects.get(QR_CODE_DATA=QR_CODE_DATA)
        except:
            return Response(json.dumps({"Message": "Customer not found"}, cls=PythonJSONEncoder), status=404,
                        content_type="application/json")
        try:
            # Check if any transaction is there for this user
            Transaction.objects.get(QR_CODE_DATA=QR_CODE_DATA)
            try:
                # Check for active transaction
                if Transaction.objects.filter(QR_CODE_DATA=QR_CODE_DATA, active=True).count() == 1:
                    raise Error
            except:
                # 409 for conflict
                return Response(json.dumps({"Message": "Already an active transaction found for current user"}, cls=PythonJSONEncoder), status=409,
                            content_type="application/json")
        except:
            pass # No Transactions yet for this user
        x = create_single_customer_dict(SingleCustomer)
        ParkingLot_obj = ParkingLot.objects.get(parking_lot_name=parking_lot_name)
        if vehicle_type == 'two_wheeler':
            if ParkingLot_obj.current_two_wheeler == ParkingLot_obj.two_wheeler_capacity - 1:
                ParkingLot_obj.current_two_wheeler += 1
            else:
                return Response(json.dumps({"Message": "Parking Full for two_wheeler"}, cls=PythonJSONEncoder), status=409,
                            content_type="application/json")

        elif vehicle_type == 'four_wheeler':
            if ParkingLot_obj.current_four_wheeler == ParkingLot_obj.four_wheeler_capacity - 1:
                ParkingLot_obj.current_four_wheeler += 1
            else:
                return Response(json.dumps({"Message": "Parking Full for four_wheeler"}, cls=PythonJSONEncoder), status=409,
                            content_type="application/json")

        elif vehicle_type == 'heavy_vehicles':
            if ParkingLot_obj.current_heavy_vehicles == ParkingLot_obj.heavy_vehicles_capacity - 1:
                ParkingLot_obj.current_heavy_vehicles += 1
            else:
                return Response(json.dumps({"Message": "Parking Full for heavy_vehicles"}, cls=PythonJSONEncoder), status=409,
                            content_type="application/json")
        ParkingLot_obj.save()
        # There is space in parking, so create transaction for current user
        t = Transaction()
        t.active = True
        t.QR_CODE_DATA = QR_CODE_DATA
        t.entry_time_stamp = datetime.now()
        t.cost = ParkingLot_obj.cost
        t.parking_lot_name = ParkingLot_obj.parking_lot_name
        t.save()

        return Response(json.dumps(x, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")


@app.route('/api/customer/exit', methods=['GET', 'POST'])
@app.route('/api/customer/exit/', methods=['GET', 'POST'])
def make_transaction_on_exit():
    if request.method == 'POST':
        json_request = json.loads(request.data)
        try:
            if not check_auth(json_request['token']):
                return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                            content_type="application/json")
        except KeyError:
            return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                            content_type="application/json")
        try:
            QR_CODE_DATA = json_request['QR_CODE_DATA']
            vehicle_type = json_request['vehicle_type']
            parking_lot_name = json_request['parking_lot_name']
        except:
            return Response(json.dumps({"Message": "QR_CODE_DATA or vehicle_type or parking_lot_name not present"}), status=400,
                                content_type="application/json")
        try:
            c = Customer.objects.get(QR_CODE_DATA=QR_CODE_DATA)
        except:
            return Response(json.dumps({"Message": "Customer does not exist"}, cls=PythonJSONEncoder), status=404,
                    content_type="application/json")

        # Checking if there is any active transaction
        try:
            transaction = Transaction.objects.get(QR_CODE_DATA=QR_CODE_DATA, active=True)
        except:
            return Response(json.dumps({"Message": "No active transaction found"}, cls=PythonJSONEncoder), status=404,
                    content_type="application/json")

        ParkingLot_obj = ParkingLot.objects.get(parking_lot_name=parking_lot_name)
        cost_obj = Cost.objects.get(parking_lot_name=parking_lot_name)
        print "1"
        if not transaction.exit_time_stamp:
            print "YES"
            if parking_lot_name != ParkingLot_obj['parking_lot_name']:
                return Response(json.dumps({"Message": "You entered from somewhere else"}, cls=PythonJSONEncoder), status=409,
                    content_type="application/json")
            transaction['exit_time_stamp'] = datetime.now()
            transaction.cost = Cost.objects.get(parking_lot_name=parking_lot_name)
            td = transaction['exit_time_stamp'] - transaction['entry_time_stamp']
            total_time = (td.days * 24) + ceil(td.seconds/3600)
            # print(td.seconds//60, (td.seconds//60)%60)
            # print('total_time' + str(total_time))
            # print('transaction ' + str(transaction))
            # transaction.total_cost = 0
            if vehicle_type == 'two_wheeler':
                transaction.total_cost = cost_obj.two_wheeler * total_time
            elif vehicle_type == 'four_wheeler':
                transaction.total_cost = cost_obj.four_wheeler * total_time
            elif vehicle_type == 'heavy_vehicles':
                transaction.total_cost = cost_obj.heavy_vehicles * total_time
            print(transaction.total_cost)
            transaction.active = False
            c.latest_transaction_cost = transaction.total_cost
            transaction.save()
            c.transactions.append(transaction)

            if vehicle_type == 'two_wheeler':
                if ParkingLot_obj.current_two_wheeler >= 1:
                    ParkingLot_obj.current_two_wheeler -= 1

            elif vehicle_type == 'four_wheeler':
                if ParkingLot_obj.current_four_wheeler >= 1:
                    ParkingLot_obj.current_four_wheeler -= 1

            elif vehicle_type == 'heavy_vehicles':
                if ParkingLot_obj.current_heavy_vehicles >= 1:
                    ParkingLot_obj.current_heavy_vehicles -= 1

            c.save()
            ParkingLot_obj.transactions.append(transaction)
            ParkingLot_obj.save()
        return Response(json.dumps({"cost": transaction.total_cost}, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")


@app.route('/api/customer/transaction/cost', methods=['GET', 'POST'])
@app.route('/api/customer/transaction/cost/', methods=['GET', 'POST'])
def get_latest_transaction_cost():
    if request.method == 'POST':
        json_request = json.loads(request.data)
        try:
            if not check_auth(json_request['token']):
                return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                            content_type="application/json")
        except KeyError:
            return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                            content_type="application/json")
        try:
            QR_CODE_DATA = json_request['QR_CODE_DATA']
        except:
            return Response(json.dumps({"Message": "QR_CODE_DATA not present"}), status=400,
                                content_type="application/json")
        try:
            c = Customer.objects.get(QR_CODE_DATA=QR_CODE_DATA)
        except:
            return Response(json.dumps({"Message": "Customer does not exist"}, cls=PythonJSONEncoder), status=404,
                    content_type="application/json")
        total_cost = c.latest_transaction_cost
        return Response(json.dumps({"cost": total_cost}, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")

@app.route('/api/parking/transactions/')
def show_parking_transactions():
    parking_objects = ParkingLot.objects.all()
    x = create_parking_dict(parking_objects)
    return Response(json.dumps(x, cls=PythonJSONEncoder), status=200,
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
        if isinstance(obj, Transaction):
            return obj.get_dict()
        if isinstance(obj, Cost):
            return obj.get_dict()
        if isinstance(obj, ParkingLot):
            return obj.get_dict()
        # elif isinstance(obj, datetime):
        #     return repr(obj.isoformat())
        # elif isinstance(obj, datetime.date):
        #     return repr(obj.isoformat())
        # elif isinstance(obj, datetime.time):
        #     return repr(obj.isoformat())
        else:
            return repr(obj)
        return super(PythonJSONEncoder, self).default(obj)


def unjsonify(dct):
    if 'eid' in dct:
        dct['eid'] = round(eval(dct['eid']), 1)
    return dct


def create_dict(allCustomer):
    result = []  # Empty for each call
    for item in allCustomer:
        d = {}  # To make a dictionary for JSON Response
        d['cid'] = item.cid
        d['first_name'] = item.first_name
        d['last_name'] = item.last_name
        d['contact_no'] = item.contact_no
        d['address'] = item.address
        d['driving_licence_link'] = item.driving_licence_link
        d['vehicles'] = item.vehicles
        d['transactions'] = item.transactions
        result.append(d)
    return result

def create_single_customer_dict(SingleCustomer):
    d = {}
    d['cid'] = SingleCustomer.cid
    d['first_name'] = SingleCustomer.first_name
    d['last_name'] = SingleCustomer.last_name
    d['contact_no'] = SingleCustomer.contact_no
    d['address'] = SingleCustomer.address
    d['driving_licence_link'] = SingleCustomer.driving_licence_link
    d['vehicles'] = SingleCustomer.vehicles
    d['transactions'] = SingleCustomer.transactions
    return d

def create_parking_dict(parkingObjects):
    d = {}
    res = []
    for item in parkingObjects:
        d['parking_lot_name'] = item.parking_lot_name
        d['transactions'] = item.transactions
        res.append(d)
    return res


def check_auth(token):
    if token == "WyIxIiwiY2UwZWY0MDFjYTA3MmJlODcyODkzYjYxOGQzZjk4YzUiXQ.B5e5Sg.qcsDcaMgiRqx21YTC0OwwnihINM":
        return True
    else:
        return False
