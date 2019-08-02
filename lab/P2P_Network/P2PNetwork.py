import socket
import threading

PORT = 7777

def ipAddress():
    f = open("E:/coding/lab/ip.txt", 'r')
    line = f.readlines()
    return line

def listening():
    print("listen")
    sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sc.bind(("", PORT))
    while True:
        sc.listen(1)
        content, addr = sc.accept()
        data = content.recv(1024)
        print(data)

class Send (threading.Thread):
    def __init__(self, ip, data):
        threading.Thread.__init__(self)
        self.ip = ip
        self.data = data

    def run(self):
        sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sc.connect((self.ip, PORT))
        print("coonected to "+ self.ip)
        sc.send(self.data.encode())
        sc.close()

for ip in ipAddress():
    t1 = Send(ip.rstrip('\n'), "hello-world-this is jungwon")
    t1.start()


t2 = threading.Thread(target=listening)
t2.start()