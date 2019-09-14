from Crypto.Hash import SHA
from Crypto.PublicKey import RSA
from Crypto import Random

def create_key():
    key_length = 1024
    random_generator = Random.new().read
    private_key = RSA.generate(key_length, random_generator)
    f = open('key.pem', 'wb+')
    f.write(private_key.exportKey('PEM'))
    f.close()

def readKey():
    try:
        f = open('key.pem','r')
        key = RSA.importKey(f.read())
        f.close()
        return key

    except:
        print("key error")

def encrypt(data):
    private_key = readKey()
    public_key = private_key.publickey()
    encrypt_data = public_key.encrypt(data, 32)

    return encrypt_data

def decrypt(data):
    private_key = readKey()
    decrypt = private_key.decrypt(data)

    return decrypt

def sign(data):
    private_key = readKey()
    public_key = private_key.publickey()
    hash = SHA.new(data).digest()
    signature = private_key.sign(hash, '')

    return hash, signature, public_key

def validation(data, signature, public_key):
    hash = SHA.new(data).digest()
    validation = False

    if public_key.verify(hash, signature):
        validation = True
        return validation
    else:
        validation = False
        return validation

