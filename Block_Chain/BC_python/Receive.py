import socket

import os

import Block
import KeyGenerator
import ProofOfWork
import os.path

def receive():
    port = 7777
    save = []

    while True:
        sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sc.bind(('', port))
        sc.listen(1)
        content, addr = sc.accept()
        data =content.recv(1024).decode('utf-8')
        if data == "bc":
            if os.path.isfile("key.pem") ==False:
                KeyGenerator.create_key()
            print("block!")
            f = open('chat.txt', 'a+t')
            f.writelines(str(save))
            f.write('\n')
            pow =  ProofOfWork.pow()
            if pow == True:
                Block.brick()
                break


        else:
            print(data)
            save.append(data)

