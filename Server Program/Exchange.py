import json
import socket, select
from Account import Account
from Mysql import Mysql

REGISTER = 100
LOGIN = 101
RECOVER_PASSWORD = 102

class Exchange:

    def receive(self, msg):
        format_msg = json.dumps(msg)
        return json.loads(format_msg)

    def send(self, dict_msg, sock):
        format_msg = json.dumps(dict_msg)
        sock.send(format_msg)

    """ Parses the received message and performs corresponding actions """
    def process(self, data):
        ## Will finish other actions once we update the JSON format
        print("A: Register")
        msg = json.loads(data)
        print(msg)
        response = self.register(msg)
        return(response)
        """
        if msg["action"] == REGISTER:
            print("A: Register")
            self.register(msg)
        if msg["action"] == LOGIN:
            print("A: Login")
            self.login(msg)
        """

    """ Register action """
    def register(self, msg):
        username = msg["username"]
        password = msg["password"]
        email = msg["email"]
        name = msg["name"]
        dob = str(msg["date_of_birth"])
        response = Account().register(username, password, email, name, dob)
        return(response)

    """ Login action """
    def login(self, msg):
        username = msg["username"]
        password = msg["password"]
        response = Account().login(username, password)
        return(response)
    
    """ Change Password action """
    def change_password(self, msg):
        username = msg["username"]
        password = msg["password"]
        response = Account().change_password(username, password)
        return(response)
