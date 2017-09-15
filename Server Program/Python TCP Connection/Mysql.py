import MySQLdb

DB_ADDRESS = "localhost"
DB_USER = "root"
DB_PASSWORD = "ht!!!"
DB_DEFAULT = "house_tarth"

class Mysql:
    def __init__(self):
        self.db = MySQLdb.connect(DB_ADDRESS,DB_USER,DB_PASSWORD,DB_DEFAULT)
    def conn(self):
        data = self.execute("SELECT VERSION()")
        print "MySQL database connected, version : %s " % data
    def execute(self, sql_statement):
        cursor = self.db.cursor()
        try:
            cursor.execute(sql_statement)
            self.db.commit()
        except:
            self.db.rollback()
        return cursor.fetchall()
    def close(self):
        self.db.close()
