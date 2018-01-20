import socket

HOST = '172.30.1.14'  # localhost

PORT = 50007  # 서버와 같은 포트사용

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # 소켓생성

s.connect((HOST, PORT))

s.send(b'Hello, python')  # 문자를 보냄

data = s.recv(1024)  # 서버로 부터 정보를 받음

s.close()

print('Received', repr(data))