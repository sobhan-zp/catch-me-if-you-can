from Mysql import Mysql

""" Handles database transactions corresponding to the account table """
class Account:

    """ Registers a new user """
    def register(self, db, username, password, email, name, dob):
        query = "INSERT INTO account VALUES (DEFAULT, '" + username + "', '" + password + "', '" + email + "', '" + name + "', " + dob + ", 1);"
        response = db.execute(query)
        print("D: " + response)
        return(response)

    """ Verifies the login credentials of a user """
    def login(self, db, username, password):
        query = "SELECT id from account WHERE username = '" + username + "' AND password = '" + password + "';"
        response = db.execute(query)
        print("D: " + response)
        return response

    """ Updates an account holder's password """
    def change_password(self, db, username, password):
        query = "UPDATE account SET account.password = '" + password + " WHERE username = '" + username + "';"
        response = db.execute(query)
        print("D: " + response)
        return response
