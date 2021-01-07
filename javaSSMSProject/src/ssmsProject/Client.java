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
    };

    public static final HashMap<Integer,String> modeMapMenu = new HashMap<Integer,String>();
    {
        modeMapMenu.put(5,"CFB128");
    };

    public static final HashMap<Integer,String> padMapMenu = new HashMap<Integer,String>();
    {
        padMapMenu.put(0,"NoPadding");
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
            System.out.println("teste: " + algMapMenu);
            System.out.println("Algoritmos de criptografia disponíveis: ");

            c.algMapMenu.entrySet().forEach(entry->{
                System.out.println(entry.getKey() + " = " + entry.getValue());
            });

            while(true){
                System.out.print("Número do algoritmo: ");
                algoritmo = input.nextInt();
                if(c.algMapMenu.containsKey(algoritmo)) {
                    prot.setAlgoritmo((byte) algoritmo);
                    break;
                }else{
                    System.out.println("Algoritmo inválido!");
                }
            }

            System.out.println("Modos disponíveis: ");
            c.modeMapMenu.entrySet().forEach(entry->{
                System.out.println(entry.getKey() + " = " + entry.getValue());
            });

            while(true){
                System.out.print("Número do modo: ");
                modo = input.nextInt();
                if(c.modeMapMenu.containsKey(modo)) {
                    prot.setModo((byte) modo);
                    break;
                }else{
                    System.out.println("Modo inválido!");
                }
            }

            System.out.println("Tipos de padding disponíveis: ");
            c.padMapMenu.entrySet().forEach(entry->{
                System.out.println(entry.getKey() + " = " + entry.getValue());
            });

            while(true){
                System.out.print("Número do padding: ");
                padding = input.nextInt();
                if(c.padMapMenu.containsKey(padding)) {
                    prot.setPadding((byte) padding);
                    break;
                }else{
                    System.out.println("Padding inválido!");
                }
            }

            System.out.print("Mensagem: ");
            prot.setMensagem(input.next());

//            prot.setAlgoritmo((byte) 0);
//            prot.setPadding((byte) 0);
//            prot.setModo((byte) 5);
//            prot.setMensagem("Teste simples de mensagem...");

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
