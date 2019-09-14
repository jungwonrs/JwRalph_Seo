import socket
import threading
from multiprocessing import Process

import Send, Receive

def ip_list():
    ip = []
    ip.append('192.168.0.16')
    #ip.append('192.168.0.20')
    #ip.append('192.168.0.31')
    #ip.append('1111111111111')
    return ip


def receive():
    while True:
        receive_data = Receive.receive()

def send():
    while True:
        input_data = input()

        for ip in ip_list():
            send_data = Send.send(ip, input_data)
            send_data.connect()


if __name__ == "__main__":

    t1 = threading.Thread(target=receive)
    t2 = threading.Thread(target=send)

    t1.start()
    t2.start()

    t1.join()
    t2.join()
