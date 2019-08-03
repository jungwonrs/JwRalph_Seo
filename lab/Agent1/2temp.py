import json
from collections import OrderedDict
from json import JSONEncoder
from time import time

from Cryptodome.Hash import SHA256 as SHA
from Cryptodome.PublicKey import RSA
from Cryptodome.Signature import pkcs1_15






private_key = RSA.generate(2048)
#print(private_key)

public_key = private_key.publickey().exportKey(format='PEM')
E_P = public_key.decode()
print(public_key)
print(type(E_P))
p_key = RSA.importKey(E_P)
print(p_key)
print(type(p_key))
print("heelo")






hash = SHA.new("hello".encode())
#print(hash.digest())

sig = pkcs1_15.new(private_key).sign(hash)
b = list(sig)
# print(sig)
# print("hell1")
# print(b)
# print("hell2")
c = bytes(b)
# print(c)
#print(type(sig))

#VObj = pkcs1_15.new(public_key)

try:
    pkcs1_15.new(p_key).verify(hash, c)
    print ("good")
except (ValueError, TypeError):
    print ("not good")



#s = json.dumps(tx_map)

#print(type(s))
#print(s)

#print(json.dumps(temp2, default=json_defualt))
#s = json.dumps(temp2.json(time(),hash,sig))