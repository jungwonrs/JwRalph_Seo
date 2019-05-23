import socket
from threading import Thread

import txManager

HOST = 'localhost'
PORT = 9009


def rcvMsg(sock):
    while True:
        try:
            data = sock.recv(1024)
            if not data:
                break
            print(data.decode())
        except:
            pass


def runChat():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.connect((HOST, PORT))
        t = Thread(target=rcvMsg, args=(sock,))
        t.daemon = True
        t.start()

        while True:
            msg = input()

########### 요 부분 중요
            if msg == 'tx':
                txManager.a()
            else:
                sock.send(msg.encode())


runChat()

