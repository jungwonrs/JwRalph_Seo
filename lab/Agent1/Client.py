import json
import pickle
import socket
import io
from time import time

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
            data = json.loads(sock.recv(4096))
            if not data:
                break
            dataParse(data)
        except:
            pass

def send():
    pri_key = RSA.generate(2048)
    pub_key = pri_key.publickey().exportKey(format='PEM')

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.connect((HOST, PORT))
        t = Thread(target=receive, args=(sock,))
        t.daemon = True
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

            tem = json.dumps(tx, indent=4)

            sock.sendall(tem.encode())


def dataParse(data):
    time = data['time']
    sig = bytes(data['sig'])
    pub_key = RSA.importKey(data['pub_key'])
    msg = data['msg']

    txVerify(time, sig, pub_key, msg)

def txVerify(time, sig, pub_key, msg):
    hash = SHA.new(msg.encode())

    try:
        pkcs1_15.new(pub_key).verify(hash, sig)
        verify = True

    except (ValueError, TypeError):
        verify = False

    #print(verify)
    if verify == True:
        print(verify)
        txPool(time, msg)
    else:
        print("verification is failed. ")


def txPool(time, msg):
    pool.append([time,msg])
    sorted(pool, key=lambda time: time[0] )
    print(pool)

send()