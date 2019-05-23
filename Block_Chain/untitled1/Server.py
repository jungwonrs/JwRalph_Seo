import socketserver
import threading

import txManager

HOST = 'localhost' #Ip Blockchain server로 진행할 IP
PORT = 9009
lock = threading.Lock()


class UserManager:

    def __init__(self):
        self.users = {}

    def addUser(self, username, conn, addr):
        if username in self.users:
            conn.send('already registered \n'.encode())
            return None

        lock.acquire()
        self.users[username] = (conn, addr)
        lock.release()

        self.sendMessageToAll('[%s] is join.' % username)
        print('+++ Number of Participation [%d]' % len(self.users))

        return username

    def removeUser(self, username):
        if username not in self.users:
            return

        lock.acquire()
        del self.users[username]
        lock.release()

        self.sendMessageToAll('[%s] is quit.' % username)
        print('--- Number of Participation [%d]' % len(self.users))


    def sendMessageToAll(self, msg):
        for conn, addr in self.users.values():
            conn.send(msg.encode())


class MyTcpHandler(socketserver.BaseRequestHandler):
    userman = UserManager()

    def handle(self):
        print('[%s] is connected' % self.client_address[0])

        try:
            username = self.registerUsername()
            msg = self.request.recv(1024)
            while msg:
                print(msg.decode())
                if self.userman.messageHandler(username, msg.decode()) == -1:
                    self.request.close()
                    break
                msg = self.request.recv(1024)

        except Exception as e:
            print(e)

        print('[%s] Termination' % self.client_address[0])
        self.userman.removeUser(username)

    def registerUsername(self):
        while True:
            self.request.send('role:'.encode())
            username = self.request.recv(1024)
            username = username.decode().strip()
            if self.userman.addUser(username, self.request, self.client_address):
                return username


class ChatingServer(socketserver.ThreadingMixIn, socketserver.TCPServer):
    pass


def runServer():
    try:
        server = ChatingServer((HOST, PORT), MyTcpHandler)
        server.serve_forever()
    except KeyboardInterrupt:
        server.shutdown()
        server.server_close()


runServer()
