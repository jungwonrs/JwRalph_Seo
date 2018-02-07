'''
save = []
length = len(save)

while length < 10:
    input_message = input()
    save.append(input_message)
    length = len(save)


f = open('chat.txt', 'a+t')
f.writelines(str(save))
f.write('\n')
'''

import keyGenerator

h = open('chat.txt','r')
message = h.read()
data = keyGenerator.encrypt(message.encode('utf-8'))
print(data)


