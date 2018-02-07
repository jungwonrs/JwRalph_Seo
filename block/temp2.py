import json

import keyGenerator

def encrypt_storage():
    save = []
    length = len(save)

    while length < 10:
        input_message = input()
        save.append(input_message)
        length = len(save)

    j_save = json.dumps(save)
    encrypt_data, hash, public_key, signature = keyGenerator.encrypt(j_save.encode('utf-8'))

    return j_save, encrypt_data, hash, public_key, signature


def decrypt_storage():
    j_save, encrypt_data, hash, public_key, signature = encrypt_storage()
    return keyGenerator.validation(j_save.encode('utf-8'), public_key,signature, encrypt_data)


def blcok():
    j_save, encrypt_data, hash, public_key, signature = encrypt_storage()
    print(hash)
    bl ={

    }

blcok()