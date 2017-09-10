from Mysql import Mysql

""" Handles database transactions corresponding to the account table """
class Account:

    """ Registers a new user """
    def register(self, username, password, email, name, dob):
        db = Mysql()
        db.conn()

        query = "INSERT INTO account VALUES (DEFAULT, '" + username + "', '" + password + "', '" + email + "', '" + name + "', " + dob + ", 1);"
        print(query)
        
        response = db.execute(query)
        print("D: " + response)
        return(response)

    """ Verifies the login credentials of a user """
    def login(self, username, password):
        db = Mysql()
        db.conn()
        query = "SELECT id from account WHERE username = '" + username + "' AND password = '" + password + "';"
        response = db.execute(query)
        print("D: " + response)
        return response

    """ Updates an account holder's password """
    def change_password(self, username, password):
        db = Mysql()
        db.conn()
        query = "UPDATE account SET account.password = '" + password + " WHERE username = '" + username + "';"
        response = db.execute(query)
        print("D: " + response)
        return response
