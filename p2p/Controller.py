import socket
import threading

import Send, Receive

def ip_list():
    ip = []
    ip.append('172.30.1.14')
    ip.append('172.30.1.34')
    #ip.append('localhost')
    #ip.append('1111111111111')
    return ip


def receive():
    while True:
        receive_data = Receive.receive()


def send():
    host_ip = socket.gethostbyname(socket.gethostname())

    while True:
        for ip in ip_list():
            if host_ip != ip:
                test_data = input()
                send_data = Send.send(ip, test_data)
                send_data.connect()



t1 = threading.Thread(target=receive)
t2 = threading.Thread(target=send)

t1.start()
t2.start()
