/*
* Integrantes:
* Igor Inácio de Carvalho Silva - 725804
* Vitoria Rodrigues Silva - 726598
* */
package ssmsProject;

import protocols.RunProtocol;

import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Client {

    public static final HashMap<Integer,String> algMapMenu = new HashMap<Integer,String>();
    {
        algMapMenu.put(0,"AES128"); // 128 bits
        algMapMenu.put(1,"AES192"); // 192 bits
        algMapMenu.put(2,"AES256"); // 256 bits
        algMapMenu.put(3,"DES"); // 64 bits
        algMapMenu.put(4,"3DES-EDE2"); // 64 bits
        algMapMenu.put(5,"3DES-EDE3"); // 64 bits
    };

    public static final HashMap<Integer,String> modeMapMenu = new HashMap<Integer,String>();
    {
        modeMapMenu.put(0,"ECB");
        modeMapMenu.put(1,"CBC");
        modeMapMenu.put(2,"CFB1");
        modeMapMenu.put(3,"CFB8");
        modeMapMenu.put(4,"CFB64");
        modeMapMenu.put(5,"CFB128");
        modeMapMenu.put(6,"CTR");
    };

    public static final HashMap<Integer,String> padMapMenu = new HashMap<Integer,String>();
    {
        padMapMenu.put(0,"NoPadding");
        padMapMenu.put(1,"PKCS5Padding");
    };

    public static void main(String[] args) {
        int algoritmo, modo, padding;
        String mensagem;

        // Instancia protocolo no modo cliente
        ProtocolSSMS prot= new ProtocolSSMS(ProtocolSSMS.CLIENT);

        // Referencia para socket TCP

        Socket sk = null;
        // Referencia para executor do protocolo implementado com ProtocolModel
        RunProtocol run = null;

        // Scanner para leitura de dados
        Scanner input = new Scanner(System.in);

        //Necessário para a utilização do hashmap
        Client c = new Client();

        try {
            sk = new Socket("localhost", 50000);
            run = new RunProtocol(sk);

            prot.setOrigem((short) 0);
            prot.setDestino((short) 26598);

            System.out.println("Algoritmos de criptografia disponíveis: ");
            c.algMapMenu.entrySet().forEach(entry->{
                System.out.println(entry.getKey() + " = " + entry.getValue());
            });

            System.out.print("Número do algoritmo: ");
            algoritmo = input.nextInt();

            prot.setAlgoritmo((byte) algoritmo);

            System.out.println("Modos disponíveis: ");
            c.modeMapMenu.entrySet().forEach(entry->{
                System.out.println(entry.getKey() + " = " + entry.getValue());
            });

            System.out.print("Número do modo: ");
            modo = input.nextInt();

            prot.setModo((byte) modo);

            System.out.println("Tipos de padding disponíveis: ");
            c.padMapMenu.entrySet().forEach(entry->{
                System.out.println(entry.getKey() + " = " + entry.getValue());
            });

            System.out.print("Número do padding: ");
            padding = input.nextInt();

            prot.setPadding((byte) padding);

            input.nextLine();
            System.out.println("Mensagem: ");
            prot.setMensagem(input.nextLine());

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
