import socket
import threading

import Send, Receive

def ip_list():
    ip = []
    ip.append('localhost')
    #ip.append('192.168.0.20')
    #ip.append('192.168.0.31')
    #ip.append('1111111111111')
    return ip


def receive():
    while True:
        receive_data = Receive.receive()

def send():
    host_ip = socket.gethostbyname(socket.gethostname())

    while True:
        test_data = input()
        for ip in ip_list():
            if host_ip != ip:
                send_data = Send.send(ip, test_data)
                send_data.connect()


t1 = threading.Thread(target=receive)
t2 = threading.Thread(target=send)

t1.start()
t2.start()

