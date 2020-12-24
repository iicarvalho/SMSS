import socket
import pickle
import sys
import time

'''
############ TIPOS DE MENSAGEM ############
Código - Tipo - Descrição
0 - Par_req - Requisição de verificação de parâmetros de criptografia
1 - Par_conf - Confirmação: parâmetros aceitos
2 - Dados - Transmissão de dados
3 - Lista - Listagem de parâmetros suportados
4 - Conf - Confirma recepção dos dados e finaliza conexão
5 - reject - Rejeita os parâmetros e informa os algoritmos suportados

############### ALGORITMOS ###############
    Código - Tipo - Descrição
    0 - AES128 - AES com chave de 128 bits
    1 - AES192 - AES com chave de 192 bits
    2 - AES256 - AES com chave de 256 bits
    3 - DES - DES
    4 - 3DES-EDE2 - Triple-DES no modo EDE com duas chaves
    5 - 3DES-EDE3 - Triple-DES no modo EDE com três chaves

'''

def menu():
    print('###################### MENU ######################')
    print('\n  1 - Enviar mensagem para o servidor')
    print('  2 - Fechar conexão')
    print('##################################################')
    return int(input('Opção: '))

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect the socket to the port where the server is listening
server_address = ('localhost', 50002)
print('Conectando a {} porta {}'.format(*server_address))
sock.connect(server_address) 

class Message:
    def __init__(self, tipo, origem, destino, algoritmo, modo, preenchimento):
        self.tipo = tipo
        self.orige = origem
        self.destino = destino
        self.algoritmo = algoritmo
        self.modo = modo
        self.preenchimento = preenchimento

op = menu()
while(True):
    
    if (op == 2):
        print('Fechando comunicação')
        break
    elif (op == 1):
        #par_rek 
        algoritmo = input('Algoritmo:')
        operacao = input('Modo de Operação:')
        preenchimento = input('Preenchimento:')
        mensagemRequisicao = Message(0, None, None, None,'modo', 'preenchimento')
        encoded = pickle.dumps(mensagemRequisicao)
        #Envio
        sock.sendall(encoded)
        
        print('Aguardando resposta do servidor...')
        time.sleep(2)

        '''
        recebido = 0
        esperado = len(encoded)

        while recebido < esperado:
            mensagem = sock.recv(1440)
            amount_received += len(mensagem)
            print('Mensagem de')
        '''
        
        mensagemServidor = pickle.loads(sock.recv(2048))
        
        #Confirmação
        if (mensagemServidor.tipo == 1):
            print('Recebendo mensagem. Tipo: parConf')
            mensagemDados = Message(2, None, None, None, None, None)
            #cifrar mensagem de acordo com o algoritmo
            #encriptografar()
            encoded = pickle.dumps(mensagemDados)
            sock.sendall(encoded)

            print('Aguardando resposta do servidor...')
            time.sleep(2)

            mensagemServidor = pickle.loads(sock.recv(2048))

            if(mensagemServidor.tipo == 3):
                print('Recebendo mensagem. Tipo: conf')
                #fechar conexão
                print('Encerrando comunicação')
                sock.close()
                break
            else:
                print('Erro: Esperado mensagem do tipo \'conf\'')
        #Rejeição dos parâmetros
        elif(mensagemServidor.tipo == 5):
            print('Recebendo mensagem. Tipo: reject')
        else:
            print('Tipo de mensagem inválido')
    else:
        print('Opção inválida')
        op = menu()