import hashlib

import time

def pow():
    nonce = 0
    example_string = "test"
    hash = hashlib.sha256()
    start = time.time()
    search = False

    while search == False:
        nonce += 1
        text = ''.join(example_string+str(nonce))
        hash.update(text.encode('utf-8'))
        result = hash.hexdigest()

        if result.startswith('00001'):
            duration = time.time() - start
            print(duration)
            print(result)
            print("start to make a block for this chat")
            search = True
            return search