package protocols;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * This class execute protocols defined using Protocol interface in a socket.
 *
 * @author Yeda Regina Venturini
 */
public class RunProtocol {
    private Socket skt;

    /**
     * Constructor defines the socket to execute the protocols.
     *
     * @param s The socket for communication.
     */
    public RunProtocol(Socket s) {
        this.skt = s;
    }

    /**
     * Executes a protocol. It is not allowed empty messages in the protocol.
     * Emtpy messages are treated as abort message.
     * This restriction may be removed if the protocol defines its own abort message.
     *
     * Note: Developers must be carefull when using this method directly. If the protocol need to be run in
     * a secure channel (Ciphered Streams) the input and output streams used during the communication cannot
     * be changed anytime while running the protocol.
     *
     * <PRE>
     * The following rules are taking place:
     * - The protocol is canceled and an abort message is sent if the protocol returns an null message to be send.
     * - The protocol is canceled and an abort message is sent if the protocol does not accept the received message.
     * - The protocol is canceled if an abort messagem is received.
     * </PRE>
     * @return True if the protocol was successfully executed. False if the protocol was aborted.
     * @throw IOException If an error occurs reading or writing message or an invalid message is sent or received.
     * @throw SocketException If there is an communication error.
     * @thorw SocketTimeoutException If the connection is closed by timeout.
     * @param protocol The protocol to be executed.
     * @throws java.io.IOException If an error occurs while writing or reading data from socket.
     * @throws java.net.SocketException If there is an error in the underlying protocol, such as a TCP error.
     * @throws java.net.SocketTimeoutException If the socket timeout is reached.
     */
    public boolean runProtocol(Protocol protocol) throws IOException, SocketException, SocketTimeoutException {
        OutputStream outputStream = skt.getOutputStream();
        InputStream inputStream = skt.getInputStream();
        byte[] msg = null;

        while (true) {
            if (protocol.nextStep() == Protocol.SEND_STEP) {
                msg = protocol.getMessage();
                if (msg != null) {
                    outputStream.write(msg);
                    outputStream.flush();
                    System.out.println("MESSAGE SENT ... ("+msg.length+")");
                    //Util.viewHex(msg);
                } else {
                    //System.out.println("MESSAGE NOT SENT ... ABORT");
                    return false;
                }
            } else if (protocol.nextStep() == Protocol.RECEIVE_STEP) {
                try {
                    msg = this.readSequence(inputStream, protocol);
                } catch (IOException ioe) {
                    throw ioe;
                }
                System.out.println("MESSAGE RECEIVED ... ("+msg.length+")");
                //Util.viewHex(msg);
                if (!protocol.setMessage(msg)) {
                    //System.out.println("MESSAGE NOT ACCEPTED ... ABORT");
                    return false;
                }
            } else if (protocol.nextStep() == Protocol.WAIT_STEP) {
                protocol.jumpStep();
                //Do nothing
            } else {
                return true;
            }
        }
    }

    /**
     * Reads the given inputstream until a special terminator character is found. Returns the read bytes in a String format.
     * @return A String containing the read sequence.
     * @param in The inputstream to be read.
     * @throws java.io.IOException If an error occurs while writing or reading data from socket.
     * @throws java.net.SocketTimeoutException If the socket timeout is reached.
     */
    public byte[] readSequence(InputStream in, Protocol prot) throws IOException, SocketTimeoutException {
        int lenP;
        int lenH = prot.getHeaderLenght();
        byte[] header = new byte[lenH];

        //System.out.println("HEADER: " + lenH);
        in.read(header);
        //Util.viewHex(header);
        lenP = prot.getPayloadLenght(header);
        byte[] payload = new byte[lenH+lenP];
        System.arraycopy(header, 0, payload, 0, lenH);
        in.read(payload, lenH, lenP);
        //System.out.println("PAYLOAD: " + lenP);
        //Util.viewHex(payload);
        return payload;
    }
}