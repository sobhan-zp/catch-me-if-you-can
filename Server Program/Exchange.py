import json
import socket, select

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
    def process(self, msg):
        if msg["action"] == REGISTER:
            self.register(msg)
        if msg["action"] == LOGIN:
            self.login(msg)
    def register(self, msg, db):
        return 0
    def login(self, msg, db):
        return 0
