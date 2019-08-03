import socketserver
import threading


HOST = ''
PORT = 9009
lock = threading.Lock()


class NodeManager:

    def __init__(self):
        self.nodes = {}

    def addNode(self, node_number, conn, addr):
        lock.acquire()
        self.nodes[node_number] = (conn, addr)
        lock.release()

        self.sendTxToAll('new node [%s]'  % node_number)

        return node_number

    def removeNode(self, node_number):
        if node_number not in self.nodes:
            return

        lock.acquire()
        del self.nodes[node_number]
        lock.release()

        self.sendTxToAll('node Number is [%s]' % node_number)

        return node_number


    def txHandler(self, node_number, msg):
        if msg[0] != '/':
            self.sendTxToAll('%s' % msg)
            return

        if msg.strip() == '/quit':
            self.removeNode(node_number)
            return -1

    def sendTxToAll(self, msg):
        for conn, addr in self.nodes.values():
            conn.send(msg.encode())

    def sendTxTo(self, conn):
        print("heelo")
        print(conn)
        conn.send("hello leader".encode())


class TcpHandler(socketserver.BaseRequestHandler):
    node = NodeManager()

    def handle(self):

        try:
            node_number = self.registerNode()
            msg = self.request.recv(4096)

            while msg:

                if self.node.txHandler(node_number, msg.decode()) == -1:
                    self.request.close()
                    break
                msg = self.request.recv(4096)

        except Exception as e:
            print(e)

    def registerNode(self):
        while True:
            node_number = str(len(self.node.nodes)+1)
            if self.node.addNode(node_number, self.request, self.client_address):
                return node_number

class Server(socketserver.ThreadingMixIn, socketserver.TCPServer):
    pass

def runServer():

    try:
        server = Server((HOST, PORT), TcpHandler)
        server.serve_forever()
    except KeyboardInterrupt:
        server.shutdown()
        server.server_close()


runServer()