#Succesful responseCode = 20(for login), 21(success register)
#Unseccess responseCode = 10(already not registered),11(user name taken)
#Parameters for request (x,name,password)

from flask import Flask
from pymongo import MongoClient
from flask import jsonify
from flask import Response
from flask import request
import time
import math
import re

app = Flask(__name__)

#handling requests
@app.route('/login')
def login():
	client = MongoClient("mongodb://localhost:27017")
	db = client.walletdb

	#db.ewallet.insert({"name":"Sumit","pass":"samuel", "counter":"false"})
	#value = db.ewallet.find_one()
	#value1 = {'name':value['name'] ,'pass':value['pass'],'argument':request.args['x']}
	res = {}	
	alpha = {'a': 41,'b': 42,'c': 36,'d': 51,'e': 35,'f': 46,'g': 91,'h': 92,'i':40 ,'j': 45,'k': 57,'l': 37,'m':38 ,'n':53 ,'o': 47,'p': 43,'q': 55,'r':39 ,'s': 48,'t': 44,'u': 62,'v': 94,'w': 95,'x': 93,'y': 96,'z': 63}
	number = {0: 56 ,1:59 ,2: 52,3: 58 ,4: 49 ,5: 97,6: 61,7: 54,8: 60,9:50 }
	Halhpa = {'A': 68,'B': 84,'C': 67,'D': 86,'E': 82,'F': 65,'G': 83,'H': 81,'I':64 ,'J': 80,'K': 74,'L': 73,'M': 85,'N': 72,'O': 75,'P': 69,'Q': 76,'R': 90,'S': 79,'T': 70,'U': 71,'V': 87,'W': 89,'X': 78,'Y': 88,'Z': 77}

	reverse_Halpha = {68:'A',84:'B',67:'C',86:'D',82:'E',65:'F',83:'G',81:'H',64:'I',80:'J',74:'K',73:'L',85:'M',72:'N',75:'O',69:'P',90:'R',79:'S',70:'T',71:'U',87:'V',89:'W',78:'X',88:'Y',77:'Z'}
	ralpha = {41:'a',42:'b',36:'c',51:'d',35:'e',46:'f',91:'g',92:'h',40:'i',45:'j',57:'k',37:'l',38:'m',53:'n',47:'o',43:'p',55:'q',39:'r',48:'s',44:'t',62:'u',94:'v',95:'w',93:'x',96:'y',63:'z'}
	rnumber = {56:0,59:1,52:2,58:3 ,49:4 ,97:5 ,61:6 ,54:7,60:8 ,50:9}
	if request.args['x'] == "1" :		#for registering user	
		temp = db.ewallet.find_one({'name':request.args['name']})
		a = request.args['password']
		a = re.sub('[^a-z]','',a)
		if 	temp is None:
			st = ""
			for i in a:
				temp1 = alpha[i]
				y = (temp1*temp1)-(2*temp1)+1
				st = st + str(y)
			db.ewallet.insert({"name":request.args['name'] ,"pass":st,"loggedin":"false" ,"cash":50 ,"last_transaction_date":None})
			res0 = {'responseMessage':'User Registered Successfully:' ,'responseCode':21}
			res = res0

		elif len(temp) is 0:	
			st = ""
			for i in a:
				temp1 = alpha[i]
				y = (temp1*temp1)-(2*temp1)+1
				st = st + str(y)
			db.ewallet.insert({"name":request.args['name'] ,"pass":st ,"loggedin":"false" ,"cash":50 ,"last_transaction_date":None})
			res0 = {'responseMessage':'User Registered Successfully:' ,'responseCode':21}
			res = res0						
		
		elif len(temp) is not 0:
			res0 = {'responseMessage':'User name already registered:' ,"responseCode":11}
			res = res0
			

	elif request.args['x'] == "2" :	#for login
		a = request.args['password']
		a = re.sub('[^a-z]','',a)
		st=""
		for i in a:
			temp1 = alpha[i]
			y = (temp1*temp1)-(2*temp1)+1
			st = st + str(y)
		check = db.ewallet.find_one({"name":request.args['name'] ,"pass":st})
		if len(check) is 0:
			res1 = {'responseMessage':'User not registered (Authentication Failed)' ,'responseCode': 10}
			res = res1
		
		if len(check) is not 0:
			db.ewallet.update({'name':request.args['name']},{ "$set":{"loggedin":"true"}})
			res2 = {'responseMessage':'Authentication Succesful', 'responseCode':20}
			res = res2

	return jsonify(res)

@app.route('/transaction')
def transaction():

	res = {}
	temp = {}
	localtime = time.asctime( time.localtime(time.time()))
	client = MongoClient("mongodb://localhost:27017")
	db = client.walletdb
	
	if request.args['x'] == "1":		#for transaction-arguments(x,name,amount)
		temp = db.ewallet.find_one({"name":request.args['name']})
		original_cash = temp['cash']
		DebAmount = request.args['amount']
		IDebAmount = float(DebAmount)
		new_cash = original_cash-IDebAmount
		if temp['loggedin'] == "true":
			db.ewallet.update({'name':request.args['name']},{'$set':{'cash': new_cash, 'last_transaction_date':localtime}})
			res = {'responseMessage':'Transaction Succesful', 'responseCode':22 ,'amountDebited': DebAmount, 'transaction_date':localtime}
		else:
			res = {'responseMessage':'Transaction failed','responseCode':12}

	
	elif request.args['x'] == '2': 		#for refunding - in any case   argument(x=2,name,refund)
		temp = db.ewallet.find_one({'name': request.args['name']})
		o_cash = temp['cash']
		r_cash = float(request.args['amount'])
		new_cash = o_cash+r_cash
		db.ewallet.update({'name':request.args['name']},{'$set':{'cash':new_cash}})
		res = {'responseMessage':'Account refunded sucessfully', 'responseCode':24 ,'creditAmount':r_cash}

	return jsonify(res)

@app.route('/logout')
def logout():
	res = {}
	client = MongoClient("mongodb://localhost:27017")
	db = client.walletdb
	db.ewallet.update({'name': request.args['name']}, {'$set':{'loggedin':'false'}})
	res = {'responseMessage':'logged out'}
	return jsonify(res)


@app.route('/profile')
def profile():
	res = {}
	temp = {}
	client = MongoClient("mongodb://localhost:27017")
	db = client.walletdb
	temp = db.ewallet.find_one({'name': request.args['name']})
	res = {'name': temp['name'], 'cash': temp['cash'], 'last_transaction_date':temp['last_transaction_date']}
	return jsonify(res)

#running the script
app.run(debug=True, host="0.0.0.0", port=8000)

