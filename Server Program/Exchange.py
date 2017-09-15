import json
import uuid
import ast
import socket, select
from Account import Account
from Mysql import Mysql

REGISTER = 100
LOGIN = 101
RECOVER_PASSWORD = 102

class Exchange:

    def receive(self, msg):
        ##format_msg = json.dumps(msg[0:len(msg)-1])
        ##return json.loads(msg[0:len(msg)-1])
        return json.loads(msg)

    def send(self, dict_msg, sock):
        format_msg = json.dumps(dict_msg)
        sock.send(format_msg)

    """ Parses the received message and performs corresponding actions """
    def process(self, acc, db, sock, data):
        ## Will finish other actions once we update the JSON format
        msg = self.receive(data)
        print(msg)
        if (type(msg)==dict):
            if ("action" in msg.keys()):
                if msg["action"] == REGISTER:
                    print("A: Register")
                    self.register(acc, db, sock, msg)
                elif msg["action"] == LOGIN:
                    print("A: Login")
                    self.login(acc, msg)
                else:
                    print("E: Wrong Action Code")
            else:
                print("E: No Action")
        else:
            print("E: Wrong Message")

    """ Register action """
    def register(self, acc, db, sock, msg):
        username = msg["username"]
        password = msg["password"]
        email = msg["email"]
        name = msg["name"]
        dob = str(msg["date_of_birth"])
        response = acc.register(db, username, password, email, name, dob)
        if response:
            data_dict = {}
            status_dict = {}
            status_dict["type"] = "success"
            status_dict["code"] = 200
            status_dict["error"] = false
            data_dict["status"] = status_dict
            self.send(data_dict, sock)

    """ Login action """
    def login(self, msg):
        username = msg["username"]
        password = msg["password"]
        response = acc.login(db, username, password)
        if response:
            data_dict = {}
            status_dict = {}
            status_dict["type"] = "success"
            status_dict["code"] = 200
            status_dict["error"] = false
            data_dict["status"] = status_dict
            data_dict["session_token"] = uuid.uuid4().hex
            self.send(data_dict, sock)
    
    """ Change Password action """
    def change_password(self, msg):
        username = msg["username"]
        password = msg["password"]
        response = Account().change_password(username, password)
        return(response)
