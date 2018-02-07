import json
from Crypto.Hash import SHA
from Crypto.PublicKey import RSA
from Crypto import Random

def createKey():
    key_length = 1024
    random_generator = Random.new().read
    private_key = RSA.generate(key_length, random_generator)
    f = open('key.pem', 'wb+')
    f.write(private_key.exportKey('PEM'))
    f.close()

def readKey():
    try:
        h = open('key.pem','r')
        key = RSA.importKey(h.read())
        h.close()
        return key
    except:
        print("key error")

def encrypt(msg):
    private_key = readKey()
    public_key = private_key.publickey()
    encrypt_data = public_key.encrypt(msg, 32)

    return encrypt_data

def decrypt(data):
    private_key = readKey()
    decrypt = private_key.decrypt(data)
    return decrypt


'''
temp = ["hello","this is  ", "OKAEY!~"]
temp2  = json.dumps(temp)
data= encrypt(temp2.encode('utf-8'))
print(data)
data2 = decrypt(data)
print(data2)
'''