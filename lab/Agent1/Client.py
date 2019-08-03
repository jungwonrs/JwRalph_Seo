import json
import random
import re
import socket
from time import time
import Agent1.Agent as Agent

from Cryptodome.PublicKey import RSA
from threading import Thread
from Cryptodome.Hash import SHA256 as SHA
from Cryptodome.Signature import pkcs1_15


HOST = 'localhost'
PORT = 9009
pool = []

def receive(sock):
    while True:
        try:
            data = sock.recv(4096)
            if not data:
                break
            dataParse(data)
        except:
            pass

def onSwitch(sock):
    try:
        first_message = sock.recv(1024).decode()
        li_node_number = re.findall("\d+", first_message)
        str_node_number = ''.join(li_node_number)

        randomSeed(str_node_number, "hello", sock)

    except:
        pass

def randomSeed(node_number, seed_value, sock):
    # self
    #set_timer = threading.Timer(10, randomSeed, args=(node_number, seed_value, sock))

    random.seed(seed_value)
    random_value = random.randint(1,1)

    if int(node_number) == random_value:
        sock.sendall("leader".encode())
    #set_timer.start()


def send():
    pri_key = RSA.generate(2048)
    pub_key = pri_key.publickey().exportKey(format='PEM')

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.connect((HOST, PORT))
        t = Thread(target=receive, args=(sock,))
        t2 = Thread(target=onSwitch, args=(sock, ))
        t.daemon = True
        t2.daemon = True
        t2.start()
        t.start()


        while True:
            msg = input()
            if msg == '/quit':
                sock.send(msg.encode())
                break

            hash = SHA.new(msg.encode())
            sig = pkcs1_15.new(pri_key).sign(hash)
            tx = {
                "time": time(),
                "sig": list(sig),
                "pub_key": pub_key.decode(),
                "msg": msg
            }
            tx_json = json.dumps(tx, indent=4)

            sock.sendall(tx_json.encode())

def dataParse(data):
    try:
        data = json.loads(data)
        print(type(data))
        time = data['time']
        sig = bytes(data['sig'])
        pub_key = RSA.importKey(data['pub_key'])
        msg = data['msg']

        txVerify(time, sig, pub_key, msg)
    except:
        data = data.decode()
        startAgent(data)


def txVerify(time, sig, pub_key, msg):
    hash = SHA.new(msg.encode())
    try:
        pkcs1_15.new(pub_key).verify(hash, sig)
        verify = True

    except (ValueError, TypeError):
        verify = False

    if verify == True:
        #print(verify)
        txPool(time, msg)
    else:
        print("verification is failed.")


def txPool(time, msg):
    pool.append([time, msg])
    sorted(pool, key=lambda time: time[0])
    print(pool)

def startAgent(data):
    startAgent = Agent
    startAgent.switchOn(data)

send()
