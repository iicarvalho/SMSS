package ssmsProject;

import protocols.RunProtocol;

import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        // Instancia protocolo no modo cliente
        ProtocolSSMS prot= new ProtocolSSMS(ProtocolSSMS.CLIENT);
        // Referencia para socket TCP
        Socket sk = null;
        // Referencia para executor do protocolo implementado com ProtocolModel
        RunProtocol run = null;

        try {
            sk = new Socket("localhost", 50000);
            run = new RunProtocol(sk);
            // Define mensagem e valor inicial
            // limitado ao valor 124 (pois valor maximo positivo de 1 byte
            // com sinal é 127 (124+3)
            // prot.setMensagem("Teste simples de mensagem...");
            // prot.setValor(17);

            System.out.println("Executando protocolo ...");
            // Chama o método que roda o protocolo do tipo ProtocolModel
            run.runProtocol(prot);
            System.out.println("Rodou no Client");
            if (prot.errorStatus()){
                System.out.println("CODIGO DE ERRO: " + prot.getErrorCode() + "\n");
            } else {
                System.out.println("Mensagem enviada com sucesso ...");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
