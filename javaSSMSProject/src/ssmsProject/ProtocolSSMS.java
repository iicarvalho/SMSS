package ssmsProject;

import protocols.ProtocolModel;
import java.nio.ByteBuffer;

public class ProtocolSSMS extends ProtocolModel {

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
//        public int getHeaderLenght () {
//            return 0;
//        }

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

    private byte[] genParReq() {
        /*
        * ESPECIFICAÇÃO PAR_REQ:
        * TIPO = 0
        * ORIGEM = 0
        * DESTINO = 65535
        * ALGORITMO = Número de 0 a 5 (de acordo com a tabela da especificação)
        * PADDING = Número de 0 a 1 (de acordo com a tabela da especificação)
        * MODO = Número de 0 a 6
        * */
        byte tipo_reservado = 0b00000000;
        int origem = 0;
        int destino = 65535;
        byte algoritmo_padding = 0b00000001;
        byte modo = 0;

        ByteBuffer origemByte = ByteBuffer.allocate(2);
        origemByte.putInt(origem);
        byte[] origemArray = origemByte.array();

        ByteBuffer destinoByte = ByteBuffer.allocate(2);
        destinoByte.putInt(destino);
        byte[] destinoArray = destinoByte.array();

        byte[] msg = new byte[7];
        msg[0] = tipo_reservado;
        System.arraycopy(origemArray, 0, msg, 1, origemArray.length);
        System.arraycopy(destinoArray, 0, msg, origemArray.length + 1, destinoArray.length);
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
}


