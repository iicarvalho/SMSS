package ssmsProject;

import protocols.RunProtocol;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args){
        // Instancia protocolo no modo servidor
        ProtocolSSMS prot = new ProtocolSSMS(ProtocolSSMS.SERVER);
        // Referencia para socket TCP
        ServerSocket ssk = null;
        Socket sk = null;
        // Referencia para executor do protocolo implementado com ProtocolModel
        RunProtocol run = null;

        try {
            // Cria socket servidor, que escupa requisição de conexão
            ssk = new ServerSocket(50000);
            System.out.println("Aguardando conexao");

            // Aguarda requisição de conexão
            sk = ssk.accept();
            System.out.println("Conexao aceita");
            run = new RunProtocol(sk);

            // Chama o método que roda o protocolo do tipo ProtocolModel
            run.runProtocol(prot); //erro aqui
            System.out.println("Rodou no Server");

            if (prot.errorStatus()){
                System.out.println("ERRO CÓDIGO: " + prot.getErrorCode());
            } else {
                // System.out.println("\n**** MENSAGEM RECEBIDA ****\n" + prot.getMensagem() + "\n**********\n");
            }
        } catch (Exception e){
            System.out.println("Exception gerada: "+e);
            e.printStackTrace();
        }
    }
}
