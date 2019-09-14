import socket


def receive():
    port = 7777

    while True:
        sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sc.bind(('', port))
        sc.listen(1)
        content, addr = sc.accept()

        print((content.recv(1024).decode('utf-8')))
