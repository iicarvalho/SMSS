import socket
import sys
import pickle

class Message:
    def __init__(self, tipo, origem, destino, algoritmo, modo, preenchimento):
        self.tipo = tipo
        self.orige = origem
        self.destino = destino
        self.algoritmo = algoritmo
        self.modo = modo
        self.preenchimento = preenchimento

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the socket to the port
server_address = ('localhost', 50002)
print('Iniciando em {} porta {}'.format(*server_address))
sock.bind(server_address)

# Listen for incoming connections
sock.listen(1)

while True:
    # Wait for a connection
    print('Aguardando conexão do cliente')
    connection, client_address = sock.accept()
    try:
        print('Conexão de', client_address)

        # Receive the data in small chunks and retransmit it
        while True:

            message = pickle.loads(connection.recv(2048))

            if (message):

                if (message.tipo == 0):
                    print('Recebendo mensagem. Tipo: parReq')
                    #se os parâmetross são válidos, envia vetor de inicializacao (VI)
                    if (True):
                        mensagemConfirmacao = Message(1, None, None, None, None, None)
                    else:
                        mensagemConfirmacao = Message(5, None, None, None, None, None)
                        print("Algoritmos suportados:")

                elif (message.tipo == 2):
                    print('Recebendo mensagem. Tipo: dados')
                    #print('mensagem criptografada:', mensagem)
                    #desencriptografar()
                    #print('mensagem desencriptografada:', mensagem)
                    mensagemConfirmacao = Message(3, None, None, None, None, None)
            
                print('Enviando confirmação para o cliente')
                encoded = pickle.dumps(mensagemConfirmacao)
                connection.sendall(encoded)
    except:
        connection.close()
    finally:
        # Clean up the connection
        connection.close() 