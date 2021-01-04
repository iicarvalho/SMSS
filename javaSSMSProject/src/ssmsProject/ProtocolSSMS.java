package ssmsProject;

import protocols.ProtocolModel;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class ProtocolSSMS extends ProtocolModel {

    private short origem;
    private short destino;
    private byte algoritmo;
    private byte padding;
    private byte modo;

    ProtocolSSMS(int mode) {
        super(mode, 4);
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
//        public int getPayloadLenght ( byte[] header){
//            return 0;
//        }
//
//        @Override
//        protected byte[] nextMessage () {
//            return new byte[0];
//        }
//
//        @Override
//        protected boolean verifyMessage ( byte[] receivedMsg){
//            return false;
//        }
    }

    @Override
    public int getHeaderLenght () {
        int ret = 0;
        // Precisa reconhecer o tipo da mensagem e saber o tamanho
        // do cabeçalho e payload para leitura pelo RunProtocol
        //System.out.println("STEP: " + this.getStep());
        switch (this.getStep()) {
            case 0: ret = 1;break;
            case 1: ret = 1;break;
            case 2: ret = 2;break;
            case 3: ret = 1;break;
        }
        return ret;
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
        /*
        * ESPECIFICAÇÃO PAR_CONF
        * TIPO = 1
        * CÓDIGO DE ERRO = Número de 0 a 6
        * VETOR DE INICIALIZAÇÃO = (nenhum ou o correto para o alg suportado)
        */

        /* Pseudo-código */
        // Declarar os valores especificados
        // Copiar os valores acima para o vetor de bytes parConf
        byte [] parConf = new byte[17];
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
}


