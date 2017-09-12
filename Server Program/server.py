# Tcp Chat server

import socket, select
from Mysql import Mysql
from Account import Account
from Exchange import Exchange

RECV_BUFFER = 4096 # an exponent of 2
PORT = 80
MAX_CLIENT = 10
SERVER_START_INFO = "##House Tarth##"

def offline_client(sock):
    print "-------[IP:%s Port:%s] disconnected-------" % addr
    sock.close()
    conn_list.remove(sock)

if __name__ == "__main__":
    conn_list = []
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_socket.bind(("0.0.0.0", PORT))
    server_socket.listen(MAX_CLIENT)
    conn_list.append(server_socket)
    print SERVER_START_INFO
    print "TCP server starts at Port " + str(PORT)
    mysql_op = Mysql()
    mysql_op.conn()
    exchange = Exchange()
    acc = Account()

    while True:
        read_sockets,write_sockets,error_sockets = select.select(conn_list,[],[])
        for sock in read_sockets:
            #New connection
            if sock == server_socket:
                sockfd, addr = server_socket.accept()
                conn_list.append(sockfd)
                print "-------[IP:%s Port:%s] connected-------" % addr
                sockfd.send("Connected\n")

            #Message from exist client
            else:
                # Data recieved from client
                try:
                    data = sock.recv(RECV_BUFFER)
                    if data:
                        exchange.process(acc, mysql_op, sock, data)
                    else:
                        print("CLIENT TERMINATED")
                        offline_client(sock)

                except:
                    print("EXCEPTION")
                    offline_client(sock)
                    continue

    server_socket.close()
