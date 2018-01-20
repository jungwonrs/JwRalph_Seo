import socket

class send:
    def __init__(self, ip, data):
        self.ip = ip
        self.data = data

    def connect(self):
        port = 7777

        try:
            sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sc.connect((self.ip, port))
            sc. send((self.data).encode())
        except:
            print("connection error!")


