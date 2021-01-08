/*
 * Integrantes:
 * Igor Inácio de Carvalho Silva - 725804
 * Vitoria Rodrigues Silva - 726598
 * */
package protocols;

public interface Protocol {
    /** The client mode of the protocol. */
    public static final int CLIENT = 1;
    /** The server mode of the protocol. */
    public static final int SERVER = 2;
    /** The finish type step of the protocol step. */
    public static final int FINISH_STEP = 0;
    /** The receive type step of the protocol. */
    public static final int RECEIVE_STEP = 1;
    /** The send type step of the protocol. */
    public static final int SEND_STEP = 2;
    /** The wait type step of the protocol. */
    public static final int WAIT_STEP = 3;

    /** Return the type of the next step of the protocol.
     * For example, SEND_STEP, RECEIVE_STEP, FINISH_STEP.
     *
     *@return The step type.
     */
    public int nextStep();

    /** Return the current step number of the protocol.
     *
     *@return The step number.
     */
    public int getStep();

    /** Return the number of steps of the protocol.
     *
     *@return The number of steps.
     */
    public int getTotalSteps();

    /** Return the message to be sent.
     * This method will be called just when the step type is SEND_STEP.
     *
     *@return The messagem to be sent.
     */
    public byte[] getMessage();

    /** Receive and process the received message.
     * This method will be called after receiving a message in the RECEIVE_STEP.
     * If an invalid message is received the method can return false and the protocol must be interruped.
     *
     *@param message The received message.
     *@return True if a valid message was received or false otherwise.
     */
    public boolean setMessage(byte[] message) throws Exception;

    public void jumpStep();

    /** Return the number of bytes for protocol header.
     *  This method can return diferent value for each type of protocol message.
     *  It must be used before the method getPayload()
     * @return The fix lenght of the protocol header.
     */
    public int getHeaderLenght();

    /** Return the lenght of the protocol payload, which can be fix or variable.
     *  The header is provided to extract the lenght when it is variable.
     * @param header The protocol header.
     * @return The fix lenght to read after fix header.
     */
    public int getPayloadLenght(byte[] header);
}
