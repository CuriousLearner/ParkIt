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
import urllib2
from json import loads

@app.route('/')
def api_admin_panel_home():
    return redirect('/admin')

@app.route('/api/customer/register', methods=['GET', 'POST'])
@app.route('/api/customer/register/', methods=['GET', 'POST'])
def register_customer():
    if request.method == 'POST':
        token = request.headers.get('token')
        if not token:
            return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                            content_type="application/json")
        if not check_auth(token):
            return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                        content_type="application/json")
        json_request = json.loads(request.data)
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
        ewallet_reg_url = "http://0.0.0.0:8000/login?x=1&name=" + str(c.cid) + "&password=" + str(c.QR_CODE_DATA)
        content = json.loads(urllib2.urlopen(ewallet_reg_url).read())
        print(content)
        return Response(json.dumps({"QR_CODE_DATA": c.QR_CODE_DATA}), status=200,
                            content_type="application/json")

@app.route('/api/customer/')
def get_customer():
    '''
    returns all the events with GET request
    '''
    token = request.headers.get('token')
    if not token:
        return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                        content_type="application/json")
    if not check_auth(token):
        return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                    content_type="application/json")
    allCustomer = Customer.objects.all()
    result = create_dict(allCustomer)
    return Response(json.dumps(result, cls=PythonJSONEncoder), status=200,
                    content_type="application/json")


@app.route('/api/customer/cid/<int:cid>')
@app.route('/api/customer/cid/<int:cid>/')
def get_single_customer(cid):
    '''
    returns all the events with GET request
    '''
    token = request.headers.get('token')
    if not token:
        return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                        content_type="application/json")
    if not check_auth(token):
        return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
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
        token = request.headers.get('token')
        if not token:
            return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                            content_type="application/json")
        if not check_auth(token):
            return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                        content_type="application/json")
        json_request = json.loads(request.data)
        try:
            pid = json_request['pid']
            QR_CODE_DATA = json_request['QR_CODE_DATA']
            vehicle_type = json_request['vehicle_type']
        except:
            return Response(json.dumps({"Message": "pid or QR_CODE_DATA or vehicle_type not present"}), status=400,
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
                return Response(json.dumps({"Message": "Already an active transaction found for current user"}, cls=PythonJSONEncoder), 
                            status=409,
                            content_type="application/json")
        except:
            pass # No Transactions yet for this user
        x = create_single_customer_dict(SingleCustomer)
        ParkingLot_obj = ParkingLot.objects.get(pid=pid)
        if vehicle_type == 'two_wheeler':
            if ParkingLot_obj.current_two_wheeler <= ParkingLot_obj.two_wheeler_capacity - 1:
                ParkingLot_obj.current_two_wheeler += 1
            else:
                return Response(json.dumps({"Message": "Parking Full for two_wheeler"}, cls=PythonJSONEncoder), status=409,
                            content_type="application/json")

        elif vehicle_type == 'four_wheeler':
            if ParkingLot_obj.current_four_wheeler <= ParkingLot_obj.four_wheeler_capacity - 1:
                ParkingLot_obj.current_four_wheeler += 1
            else:
                return Response(json.dumps({"Message": "Parking Full for four_wheeler"}, cls=PythonJSONEncoder), status=409,
                            content_type="application/json")

        elif vehicle_type == 'heavy_vehicles':
            if ParkingLot_obj.current_heavy_vehicles <= ParkingLot_obj.heavy_vehicles_capacity - 1:
                ParkingLot_obj.current_heavy_vehicles += 1
            else:
                return Response(json.dumps({"Message": "Parking Full for heavy_vehicles"}, cls=PythonJSONEncoder), status=409,
                            content_type="application/json")
        ParkingLot_obj.save()
        # Check for balance in e-wallet
        ewallet_profile_url = "http://0.0.0.0:8000/profile?name=" + str(SingleCustomer.cid)
        content = json.loads(urllib2.urlopen(ewallet_profile_url).read())
        # Now check balance
        if content['cash'] < 40:
            return Response(json.dumps({"Message": "Balance is low in your wallet. Please recharge first"}), status=200,
                            content_type="application/json")
        # There is space in parking anc enough balance in wallet,
        # so create transaction for current user
        t = Transaction()
        t.active = True
        t.QR_CODE_DATA = QR_CODE_DATA
        t.entry_time_stamp = datetime.now()
        t.cost = ParkingLot_obj.cost
        t.pid = ParkingLot_obj.pid
        t.save()
        return Response(json.dumps(x, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")


@app.route('/api/customer/exit', methods=['GET', 'POST'])
@app.route('/api/customer/exit/', methods=['GET', 'POST'])
def make_transaction_on_exit():
    if request.method == 'POST':
        token = request.headers.get('token')
        if not token:
            return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                            content_type="application/json")
        if not check_auth(token):
            return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                        content_type="application/json")
        json_request = json.loads(request.data)
        try:
            QR_CODE_DATA = json_request['QR_CODE_DATA']
            vehicle_type = json_request['vehicle_type']
            pid = json_request['pid']
        except:
            return Response(json.dumps({"Message": "QR_CODE_DATA or vehicle_type or pid not present"}), status=400,
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

        ParkingLot_obj = ParkingLot.objects.get(pid=pid)
        parking_lot_name = ParkingLot_obj.parking_lot_name
        cost_obj = Cost.objects.get(parking_lot_name=parking_lot_name)
        if not transaction.exit_time_stamp:
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
            # print(transaction.total_cost)
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
            # Deduct balance from wallet
            # First check if there is enough balance, if not then first user would recharge
            # and then gates would open after successful deduction
            # Check for balance in e-wallet
            ewallet_profile_url = "http://0.0.0.0:8000/profile?name=" + str(c.cid)
            ewallet = json.loads(urllib2.urlopen(ewallet_profile_url).read())
            # if ewallet['cash'] < transaction.total_cost:

            # Login into wallet
            ewallet_login_url = "http://0.0.0.0:8000/login?x=2&name=" + str(c.cid) + "&password=" + str(c.QR_CODE_DATA)
            login_content = json.loads(urllib2.urlopen(ewallet_login_url).read())
            # if successful login then deduct balance
            ewallet_deduct_balance_url = "http://0.0.0.0:8000/transaction?x=1&name=" + str(c.cid) + "&amount=" + str(int(transaction.total_cost))
            dedution_content = json.loads(urllib2.urlopen(ewallet_deduct_balance_url).read())
            # Now logout from e-wallet
            ewallet_logout_url = "http://0.0.0.0:8000/logout?name=" + str(c.cid)
            logout_content = json.loads(urllib2.urlopen(ewallet_logout_url).read())
        return Response(json.dumps({"cost": transaction.total_cost}, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")


@app.route('/api/customer/transaction/cost')
@app.route('/api/customer/transaction/cost/')
def get_latest_transaction_cost():
    token = request.headers.get('token')
    if not token:
        return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                        content_type="application/json")
    if not check_auth(token):
        return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                    content_type="application/json")
    try:
        QR_CODE_DATA = request.args.get('QR_CODE_DATA')
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

@app.route('/api/parking/transactions')
@app.route('/api/parking/transactions/')
def show_all_parking_transactions():
    token = request.headers.get('token')
    if not token:
        return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                        content_type="application/json")
    if not check_auth(token):
        return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                    content_type="application/json")
    parking_objects = ParkingLot.objects.all()
    # print(parking_objects)
    x = create_parking_transactions_dict(parking_objects)
    return Response(json.dumps(x, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")

@app.route('/api/parking/<int:pid>/transactions')
@app.route('/api/parking/<int:pid>/transactions/')
def show_single_parking_transactions(pid):
    token = request.headers.get('token')
    if not token:
        return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                        content_type="application/json")
    if not check_auth(token):
        return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                    content_type="application/json")
    parking_obj = ParkingLot.objects.get(pid=pid)
    x = create_single_parking_dict(parking_obj)
    return Response(json.dumps(x, cls=PythonJSONEncoder), status=200,
                        content_type="application/json")

@app.route('/api/parking/stats/<int:pid>')
@app.route('/api/parking/stats/<int:pid>/')
def show_parking_analysis(pid):
    token = request.headers.get('token')
    if not token:
        return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                        content_type="application/json")
    if not check_auth(token):
        return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                    content_type="application/json")
    from_exit_time_stamp = request.args.get('from_exit_time_stamp')
    to_exit_time_stamp = request.args.get('to_exit_time_stamp')
    from_year, from_month, from_day = from_exit_time_stamp.split('-')
    to_year, to_month, to_day = to_exit_time_stamp.split('-')
    from_date = datetime(int(from_year), int(from_month), int(from_day))
    to_date = datetime(int(to_year), int(to_month), int(to_day))
    print(from_date, to_date)
    transactions = Transaction.objects.filter(pid=pid, exit_time_stamp__gte=from_date, exit_time_stamp__lte=to_date)
    total_cost = 0
    no_of_transactions = len(transactions)
    for transaction in transactions:
        total_cost += transaction.total_cost
    return Response(json.dumps({"total_cost": total_cost, "no_of_transactions": no_of_transactions}), status=200,
                        content_type="application/json")

@app.route('/api/parking')
@app.route('/api/parking/')
def parking_lot_details():
    token = request.headers.get('token')
    if not token:
        return Response(json.dumps({"Message": "Please supply proper credentials"}), status=400,
                        content_type="application/json")
    if not check_auth(token):
        return Response(json.dumps({"Message": "Unauthorized access"}), status=401,
                    content_type="application/json")
    parkingLotObjects = ParkingLot.objects.all()
    x = create_parking_dict(parkingLotObjects)
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
        if isinstance(obj, datetime):
            return obj.isoformat()
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

def create_parking_transactions_dict(parkingObjects):
    result = []
    for item in parkingObjects:
        d = {}
        d['pid'] = item.pid
        d['parking_lot_name'] = item.parking_lot_name
        d['transactions'] = item.transactions
        result.append(d)
    return result

def create_parking_dict(parkingObjects):
    result = []
    for item in parkingObjects:
        d = {}
        d['pid'] = item.pid
        d['parking_lot_name'] = item.parking_lot_name
        result.append(d)
    return result

def create_single_parking_dict(parking_obj):
    d = {}
    d['pid'] = parking_obj.pid
    d['parking_lot_name'] = parking_obj.parking_lot_name
    d['transactions'] = parking_obj.transactions
    return d

def check_auth(token):
    if token == "WyIxIiwiY2UwZWY0MDFjYTA3MmJlODcyODkzYjYxOGQzZjk4YzUiXQ.B5e5Sg.qcsDcaMgiRqx21YTC0OwwnihINM":
        return True
    return False
