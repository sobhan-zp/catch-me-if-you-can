from Mysql import Mysql
class Account:
    def register(self, db):
        a="INSERT INTO account (username, password, email, name, date_of_birth, user_level) VALUES ('zire9999n','xia','asd','asd','asd',1);"
        response = db.execute(a)

        print(response)

    # def change_pass(id, password, self):
    #     response = db.execute(
    #         """
    #         UPDATE account
    #         SET account.password =
    #         """
    #         + password
    #         +
    #         """
    #         WHERE account.id =
    #         """
    #         + id
    #         + """;"""
    #     )
    #     print(response)

    def login(id, password, self):
        response = db.execute(
            """
            SELECT username from account
            WHERE id =
            """
            + id
            + """
             AND password =
            """
            + password
            + """
            ;
            """
        )
        print(response)

if __name__ == "__main__":
    acc = Account()
    db = Mysql()
    db.conn()
    acc.register(db)
