import time
from pathlib import Path

import os

import keyGenerator


def body():
    save =[]
    length = len(save)

    while length <10:
        input_message = input()
        save.append(input_message)
        length = len(save)

    f = open('chat.txt', 'a+t')
    f.writelines(str(save))
    f.write('\n')

def body_encrypt():
    h = open('chat.txt', 'r')
    message = h.read()
    data = keyGenerator.encrypt(message.encode('utf-8'))

    return data

def brick():
    f = open('block.txt', 'a+t')
    if os.stat('block.txt').st_size == 0:
        dict = {
            "previous": "0",
            "time": time.time(),
            "body": body_encrypt()
        }
        f.writelines(str(dict))
        
    else:
        print("what")
brick()



