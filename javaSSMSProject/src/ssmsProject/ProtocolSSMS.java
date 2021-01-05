package ssmsProject;

import protocols.ProtocolModel;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.HashMap;

public class ProtocolSSMS extends ProtocolModel {

    private short origem;
    private short destino;
    private byte algoritmo;
    private byte padding;
    private byte modo;
    private boolean errorMsg = false;
    private SecureSuite secureSuite;
    private byte[] iv;

    ProtocolSSMS(int mode) {
        super(mode, 4);
        secureSuite = new SecureSuite((int)algoritmo, (int)modo, (int)padding);
    }

    protected byte[] nextMessage() {
        byte[] msg = null;
        switch (this.step) {
            case 0:
                //First message, generated by the CLIENT
                msg = this.genParReq();
                System.out.println("Primeira Msg sent...");
                //Util.viewHex(msg);
                break;
            case 1:
                //Second message, genereted by the SERVER
                msg = this.genParConf();
                System.out.println("Segunda Msg sent...");
                //Util.viewHex(msg);
                break;
            case 2:
                //Third message, generated by the CLIENT
                msg = this.genDadosMsg();
                System.out.println("Terceira Msg sent...");
                //Util.viewHex(msg);
                break;
            case 3:
                //Forth message, generated by the SERVER
                msg = this.genConf();
                System.out.println("Quarta Msg sent...");
                //Util.viewHex(msg);
                break;

        }
//        @Override
//        protected byte[] nextMessage () {
//            return new byte[0];
//        }
    }

    @Override
    public int getHeaderLenght () {
        int ret = 0;
        // Precisa reconhecer o tipo da mensagem e saber o tamanho
        // do cabeçalho e payload para leitura pelo RunProtocol
        //System.out.println("STEP: " + this.getStep());
        switch (this.getStep()) {
            case 0: ret = 7;break; // mensagem par_req
            case 1: ret = 17;break; // mensagem par_conf
            case 2: ret = 3;break; // mensagem dados
            case 3: ret = 1;break; // mensagem de conf
        }
        return ret;
    }

    /* Informa ao RunProtocol qual o tamanho do payload.
    *  Neste protocolo, o tamanho é zero para as duas primeiras mensagens
    *  e é informado pelo header na terceira mensagem */
    @Override
    public int getPayloadLenght ( byte[] header){
        int ret = 0;
        // precisa reconhecer o tipo da mensagem e saber o tamanho
        // do cabeçalho e payload

        switch (this.getStep()) {
            case 0: ret = 0;break;
            case 1: ret = 0;break;
            case 2:
                byte[] tamanhoMensagem = new byte[2];
                System.arraycopy(header, 1, tamanhoMensagem, 0, 2);
                ret = convertByteArrayToInt(tamanhoMensagem);
                //System.out.println("Header: "+header[1]);
                break;
            case 3: ret = 0;break;
        }
        return ret;
    }

    protected boolean verifyMessage(byte[] receivedMsg) {
        boolean ret = false;
        switch (this.step) {
            case 0:
                //Verify First message, performed by the SERVER
                ret = this.verifyParReq(receivedMsg);
                System.out.println("Primeira Msg ... " + ret);
                break;
            case 1:
                //Verify Second message, performed by the CLIENT
                ret = this.verifyParConf(receivedMsg);
                System.out.println("Segunda Msg ... " + ret);
                break;
            case 2:
                //Verify Third message, performed by the SERVER
                ret = this.verifyThirdMessage(receivedMsg);
                System.out.println("Terceira Msg ... " + ret);
                break;
            case 3:
                //Verify Third message, performed by the SERVER
                ret = this.verifyForthMessage(receivedMsg);
                System.out.println("Terceira Msg ... " + ret);
                break;
        }

        return ret;
    }

    /* Método para verificar a primeira mensagem recebida pelo SERVER (par_req) */
    private boolean verifyParReq(byte[] receivedMsg) {
        // 1a mensagem sempre correta
        boolean ret = true;
        // atualização do status do protocolo, para o caso de enviar mensagem de erro
        this.errorMsg = false;
        // atualiza o estado das informações recebidas
        byte alg_padding = receivedMsg[5];

        int[] camposAlgPadding = convertBytetoTwoInts(alg_padding);
        this.modo = receivedMsg[6];

        secureSuite.setAlg(camposAlgPadding[0]);
        secureSuite.setMode((int)modo);
        secureSuite.setPad(camposAlgPadding[1]);

        return ret;
    }

    private boolean verifyParConf(byte[] receivedMsg) {
        boolean ret = true;
        // Ler o tipo e o codigo de erro do cabeçado (1 byte)
        // Verificar qual é o código de erro correspondente
            // Caso o código = 0 (não houve erro)
            // Senão (montar switch para imprimir mensagem de erro correta)
        // retornar receiveMsg
    }

    private byte[] genParReq() {
        /* ORIGEM E DESTINO */
        /* Os valores de origem e destino possuem tamanho de 2 bytes,
         *  por isso, precisam ser alocados em um array de bytes */
        ByteBuffer origemByte = ByteBuffer.allocate(2);
        origemByte.putInt(origem);
        byte[] origemArray = origemByte.array();

        ByteBuffer destinoByte = ByteBuffer.allocate(2);
        destinoByte.putInt(destino);
        byte[] destinoArray = destinoByte.array();

        /** TIPO **/
        /* Os primeiros 4 bits são destinados para definir o tipo da mensagem
         *  que, neste caso sempre será 0 */
        byte tipo_reservado = 0b00000000;

        /* ALGORITMO E PADDING */
        /* Define o valor do byte de acordo com as combinações de algoritmo e padding
        *  O valor do algoritmo está nos primeiros 4 bits
        *  O valor do padding está nos útimos 4 bits */
        byte algoritmo_padding = 0;
        switch (algoritmo) {
            case 0:
                if (padding == 0) {
                    algoritmo_padding = 0b00000000;
                } else if (padding == 1){
                    algoritmo_padding = 0b00000001;
                }
                break;
            case 1:
                if (padding == 0) {
                    algoritmo_padding = 0b00010000;
                } else if (padding == 1){
                    algoritmo_padding = 0b00010001;
                }
                break;
            case 2:
                if (padding == 0) {
                    algoritmo_padding = 0b00100000;
                } else if (padding == 1){
                    algoritmo_padding = 0b00100001;
                }
                break;
            case 3:
                if (padding == 0) {
                    algoritmo_padding = 0b00110000;
                } else if (padding == 1){
                    algoritmo_padding = 0b00110001;
                }
                break;
            case 4:
                if (padding == 0) {
                    algoritmo_padding = 0b01000000;
                } else if (padding == 1){
                    algoritmo_padding = 0b01000001;
                }
                break;
            case 5:
                if (padding == 0) {
                    algoritmo_padding = 0b01010000;
                } else if (padding == 1){
                    algoritmo_padding = 0b01010001;
                }
                break;
            default:
                System.out.println("Erro na geração da mensagem 1 (par_req)." +
                        "O valor digitado para escolha do algortimo é inválido. " +
                        "Escolha um algortimo válido (valores de 0 a 5).");
        }

        byte[] msg = new byte[7]; // a primeira mensagem possui 7 bytes
        msg[0] = tipo_reservado;
        System.arraycopy(origemArray, 0, msg, 1, origemArray.length); // copia o valor da origem na posição msg[1]
        System.arraycopy(destinoArray, 0, msg, origemArray.length + 1, destinoArray.length); // copia o valor do destino na proxima posicao livre de msg[]
        msg[origemArray.length+1+destinoArray.length] = algoritmo_padding;
        msg[origemArray.length+1+destinoArray.length+1] = modo;

        return msg;
    }

    private byte[] genParConf() {
        byte[] parConf = new byte[17];
        byte tipo_erro = 0b00010000;
        // Verificar qual o algoritmo, modo e padding solicitados pelo cliente
        // Se o SERVER suportar essa solicitacao:
            // cria IV
            // armazena tipo + IV no vetor de byte parConf
        // Senão:
            // informar codigo de erro
        int algoritmo = secureSuite.getAlg();
        int modo = secureSuite.getMode();
        int padding = secureSuite.getPad();
        if (secureSuite.algMap.containsKey(algoritmo) && secureSuite.modeMap.containsKey(modo) &&
                secureSuite.padMap.containsKey(padding) ) {
            switch (algoritmo) {
                case 1: // aes 128
                case 2: // aes 192
                case 3: // aes 256
                    this.iv = ivGenerator(16);
                    break;
                case 4: // 3des
                case 5: // 3des-ede3
                    this.iv = ivGenerator(8);
                    break;
            }
        } else {
            tipo_erro = 0b00010001; // codigo de erro = 1
            System.out.println("Erro de parâmetros em genParConf");
        }
        parConf[0] = tipo_erro;
        System.arraycopy(iv, 0, parConf, 1, 16);
        return parConf;
    }

    private byte[] genDadosMsg() {
        /*
         * ESPECIFICAÇÃO DADOS
         * TIPO = 2
         * TAMANHO DOS DADOS = Número em bytes
         * DADOS = (MÁX DE 1140 BYTES)
         */

        /* Pseudo-código */
        // Declarar os valores especificados
        // Copiar os valores acima para o vetor de bytes dados (calcular tamanho)
        byte[] dados = new byte[];
        return dados;
    }

    private byte[] genConf() {
        /*
         * ESPECIFICAÇÃO DADOS
         * TIPO = 4
         * CÓDIGO DE ERRO = de 0 a 6
         */

        /* Pseudo-código */
        // Declarar os valores especificados
        // Copiar os valores acima para o vetor de bytes conf
        byte[] conf = new byte[1];
        return conf;
    }

    /* Conjunto de métodos SET chamados pelo CLIENT antes que o protocolo seja executado */
    public void setOrigem(short origem){
        this.origem = origem;
    }

    public void setDestino(short destino){
        this.destino = destino;
    }

    public void setAlgoritmo(byte algoritmo){
        this.algoritmo = algoritmo;
    }

    public void setPadding(byte padding){
        this.padding = padding;
    }

    public void setModo(byte modo){
        this.modo = modo;
    }

    private int convertByteArrayToInt(byte[] intBytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(intBytes);
        return byteBuffer.getInt();
    }

    /* Método recebe 1 byte do cabeçalho (campos do algoritmo e padding, cada um com  4 bits),
     transforma em bit e converte para inteiro */
    private int[] convertBytetoTwoInts(byte header) {
        byte[] eightBits = new byte[8]; // armazena 1 bit do header em cada posicao
        int[] alg_padding = new int[2]; // armazena o valor do algoritmo na posicao 0 e o valor do padding na posicao 1

        int i = 0;
        while(i < 8) { // lê 1 bit do header por vez e o armazena em eightBits
            eightBits[i] = (byte) ((header >> i) & 1);
            i++;
        }
        byte[] fourBytes = new byte[4];

        System.arraycopy(eightBits, 0, fourBytes, 0, 4);
        alg_padding[0] = convertByteArrayToInt(fourBytes); // correspode ao valor do algoritmo

        System.arraycopy(eightBits, 4, fourBytes, 0, 4);
        alg_padding[1] = convertByteArrayToInt(fourBytes); // corresponde ao valor do padding

        return alg_padding;
    }

    private byte[] ivGenerator(int tamanho) {
        byte[] iv = new byte[tamanho];
        SecureRandom srandom = new SecureRandom();
        srandom.nextBytes(iv);
        return iv;
    }
}


