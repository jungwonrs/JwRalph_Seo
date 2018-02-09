import json
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

def brick_hash(data):
    data_encoding = data.encode('utf-8')

    hash, signature, public_key = keyGenerator.sign(data_encoding)

    return str(hash), signature, public_key


def brick():
    f = open('block.txt', 'a+t')
    fr = open('block.txt', 'r')
    b = open('hash.txt', 'a+t')
    br = open('hash.txt', 'r')

    if os.stat('block.txt').st_size == 0 or os.stat('hash.txt').st_size == 0:

        dict = {
            "previous": "0",
            "time": time.time(),
            "body": body_encrypt()
        }

        f.writelines(str(dict))
        f.writelines('\n')

        hash, signature, public_key = brick_hash(str(dict))
        b.writelines(hash)
        b.writelines('\n')

#검증 검증
    else:
        last_dict = fr.readlines()[-1].rstrip()
        hash, signature, public_key = brick_hash(last_dict)
        last_hash = br.readlines()[-1].rstrip()

        if hash == last_hash:
            sign = keyGenerator.validation(last_dict.encode('utf-8'), signature, public_key)

            if sign == True:

                dict = {
                    "previous": last_hash,
                    "time": time.time(),
                    "body": body_encrypt()
                }

                f.writelines(str(dict))
                f.writelines('\n')

                hash, signature, public_key = brick_hash(str(dict))
                b.writelines(hash)
                b.writelines('\n')

            else:
                print("sign error")
        else:
            print("hash error")


brick()



