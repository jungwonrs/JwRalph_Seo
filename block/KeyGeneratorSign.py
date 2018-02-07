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

def hash(msg):
    return SHA.new(msg).digest()

def encrypt(msg):
    private_key = readKey()
    public_key = private_key.publickey()

    hash = SHA.new(msg).digest()
    signature = private_key.sign(hash, '')
    encrypt_data = public_key.encrypt(msg, 32)

    return encrypt_data, hash, public_key, signature

def validation(msg, public_key, signautre, encrypt_data):
    hash = SHA.new(msg).digest()

    if public_key.verify(hash, signautre):

        try:
            private_key = readKey()
            decrypt = private_key.decrypt(encrypt_data)
            return print(decrypt)

        except:
            print("key error")

    else:
            print("hash error")


#createKey()
#temp = ["hello","this is  ", "OKAEY!~"]
#temp2  = json.dumps(temp)
#data, hash, public_key, signature = encrypt(temp2.encode('utf-8'))
#validation(temp2.encode('utf-8'), public_key, signature,data)