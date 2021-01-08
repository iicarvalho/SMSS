/*
 * Integrantes:
 * Igor Inácio de Carvalho Silva - 725804
 * Vitoria Rodrigues Silva - 726598
 * */
package protocols;
import java.util.ArrayList;

public abstract class ProtocolModel implements Protocol {
    /** The mode the protocol must run, CLIENT or SERVER. */
    protected int mode;
    /** The current step of the protocol. */
    protected int step;
    /** The number of steps of the protocol */
    protected int nways;
    /** The protocol step sequence (SEND/RECEIVE) of messages. */
    protected ArrayList<Integer> stepSeq;
    /** The true value if an error occured. Default is false. */
    protected boolean errorStatus = false;
    /** The error code. Default is zero. */
    private int errorCode = 0;

    /** Creates a new instance of ProtocolModel.
     * The mode must be Protocol.CLIENT or Protocol.SERVER, otherwise an IllegalArgumentException will be throw.
     * The CLIENT mode must always start with SEND_STEP and the SERVER mode must always start with RECEIVE_STEP.
     * The class extending this class must define its own step sequence (see setStepSequence()).
     *
     *@param mode The mode operation of the protocol (CLIENT or SERVER).
     */
    public ProtocolModel(int mode) {
        this.mode = mode;
        if ((this.mode != CLIENT) && (this.mode != SERVER)) {
            throw new IllegalArgumentException("Illegal operation mode.");
        }
        this.step = 0;
        this.stepSeq = new ArrayList<Integer>();
    }

    /** Creates a new instance of ProtocolModel.
     * The mode must be Protocol.CLIENT or Protocol.SERVER, otherwise an IllegalArgumentException will be throw.
     * This constructor defines its own step sequence (stepSeq) with the number of steps passed as parameter (nways).
     * The SEND and RECEIVE steps are alternates in the sequence.
     * The CLIENT mode always start with SEND_STEP and the SERVER mode always start with RECEIVE_STEP.
     * A FINISH_STEP is inserted at the end of sequence of the n way protocol.
     *
     *@param mode The mode operation of the protocol (CLIENT or SERVER).
     *@param nways The number of steps of the protocol.
     */
    public ProtocolModel(int mode, int nways) {
        this.mode = mode;
        this.nways = nways;
        this.step = 0;
        this.stepSeq = new ArrayList<Integer>(nways);
        if (this.mode == CLIENT) {
            this.stepSeq.add(0, SEND_STEP);
        } else if (this.mode == SERVER) {
            this.stepSeq.add(0, RECEIVE_STEP);
        } else {
            throw new IllegalArgumentException("Illegal operation mode.");
        }
        for (int i = 1; i < nways; i++) {
            if (this.stepSeq.get(i-1) == SEND_STEP) {
                this.stepSeq.add(i, RECEIVE_STEP);
            } else {
                this.stepSeq.add(i, SEND_STEP);
            }
        }
        this.stepSeq.add(nways, FINISH_STEP);
    }

    /**
     * Defines the step sequence of the protocol.
     * <PRE>
     * Example:
     *
     *  Let�s the protocol with the following exchanged messages.
     *    A -> B
     *    A -> B
     *    A <- B
     *    A -> B
     *
     *  The following step sequence must be defined to A and B:
     *  A: stepSeq = {SEND_STEP, SEND_STEP, RECEIVE_STEP, SEND_STEP}
     *  B: stepSeq = {RECEIVE_STEP, RECEIVE_STEP, SEND_STEP, RECEIVE_STEP}
     *
     *  The correct value to each step sequence is defined by Protocol.
     *  After the last step a step with the Protocol.FINISH_STEP value is inserted by method.
     * </PRE>
     * @param stepSeq The arraylist defining the step sequence of the protocol.
     */
    public void setStepSequence(ArrayList<Integer> stepSeq) {
        this.stepSeq = stepSeq;
        this.stepSeq.add(this.stepSeq.size(), Protocol.FINISH_STEP);
    }

    /** Return the type of the next step of the protocol.
     * Possible types: SEND_STEP, RECEIVE_STEP, FINISH_STEP.
     *
     *@return The step type.
     */
    public int nextStep(){
        return stepSeq.get(this.step);
    }

    /** Return the current step number of the protocol.
     *
     *@return The step number.
     */
    public int getStep() {
        return this.step;
    }

    /** Return the number of steps of the protocol.
     *
     *@return The number of steps.
     */
    public int getTotalSteps() {
        return this.nways;
    }

    /** Return the message to be sent when the step type is SEND_STEP.
     * The retuned message must be sincronized with the protocol step.
     * If this method is called in the RECEIVE_STEP or FINISH_STEP a NULL message is returned.
     *
     *@return The messagem to be sent.
     */
    public byte[] getMessage() {
        byte[] bMsg = null;

        if (stepSeq.get(this.step) == SEND_STEP) {
            bMsg = this.nextMessage();
            this.step++;
        }
        if (bMsg == null) {
            System.out.println("ProtocolModel.getMessage(): null msg received of nextMessage() and sendErrorMessage is falso. Return null.");
        }
        //Util.viewHex(bMsg);

        return bMsg;
    }

    /** Receive and process the received message (not base64 encoded).
     * This method will be called after receiving a message in the RECEIVE_STEP.
     * This method returns false if it was called in a non RECEVE_STEP.
     * If an invalid message is received the method can return false and the protocol must be interruped.
     *
     *@param message The received message (not base64 encoded).
     *@return True if a valid message was received or false otherwise.
     */
    public boolean setMessage(byte[] message) throws Exception {

        if (stepSeq.get(this.step) == RECEIVE_STEP) {
            if (this.verifyMessage(message)) {
                this.step++;
                return true;
            } else {
                System.out.println("ProtocolModel.setMessage(): verifyMessage false and sendErrorMessage false. Return true with abortMessage true.");
                this.errorStatus = true;
                return false;
            }
        }

        return false;
    }

    /** This method return the error code if an error message is received.
     * If an error message is not received it returns -1.
     *@return Error code.
     */
    public int getErrorCode() {
        if (this.errorStatus) {
            return this.errorCode;
        }
        return -1;
    }

    /** Informe if the end of protocol was generated by an error.
     *@return True if the end of protocol was generated by an error.
     */
    public boolean errorStatus() {
        return (this.errorStatus);
    }

    /** This method defines the error message to send before finishing the communication when an error occurs.
     * The error code message will only be send if ERROR_FAIL_MSG is selected as fail message.
     *
     *@param errorCode The error code defined to communication.
     */
    protected void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /** This method can or cannot be implemented by the protocol. It receives the error status and treats the error within the specific protocol.
     *
     *@param errorCode The error code defined to communication.
     *@param step The current step
     */
    protected void treatError(int errorCode, int step) {
    }

    /** This method must return the message related to the current step.
     * If the default step sequence is used (...SEND,RECEIVE,SEND,...),
     * with CLIENT starting with SEND and SERVER with RECEIVE,
     * the first message (step 0) is the CLIENT message, the second message (step 1) is the SERVER message, and so on.
     * If the nextMessage() returns null an abort or error message is sent (if enabled) and the communication is concluded.
     * <PRE>
     *
     * Example:
     * public byte[] nextMessage() {
     *   byte[] msgbyte = null;
     *   switch(this.step) {
     *       case 0:
     *           //First message, generated by CLIENT
     *           msgbyte = genFirstMessage();
     *           break;
     *       case 1:
     *           //Second message, generated by SERVER
     *           msgbyte = genSecondMessage();
     *           break;
     *       ...
     *   }
     *   return msgbyte;
     * }
     * </PRE>
     *@return The message related to the current step.
     */
    protected abstract byte[] nextMessage();

    /** This method must process the received message and verify if it is a valid message to the current step.
     * If the default step sequence is used (...SEND,RECEIVE,SEND,...),
     * with CLIENT starting with SEND and SERVER with RECEIVE,
     * the first message (step 0) is received and processed by SERVER, the second message (step 1) is received and processed by CLIENT, and so on.
     * If the verifyMessage() returns false an abort or error message is sent (if enabled) and the communication is concluded.
     * <PRE>
     *
     * Example:
     * public boolean verifyMessage(byte[] receivedMsg) {
     *   boolean ret = false;
     *   switch(this.step) {
     *       case 0:
     *           //Verify First message, performed by SERVER
     *           ret = verifyFirstMessage(receivedMsg);
     *           break;
     *       case 1:
     *           //Verify Second message, performed by CLIENT
     *           ret = verifySecondMessage(receivedMsg);
     *           break;
     *       ...
     *   }
     *   return ret;
     * }
     * </PRE>
     *@param receivedMsg The message received by Socket to be processed.
     *@return True if the message was correctly received and it is a valid message.
     */
    protected abstract boolean verifyMessage(byte[] receivedMsg) throws Exception;

    public void jumpStep() {
        this.step++;
    }
}
