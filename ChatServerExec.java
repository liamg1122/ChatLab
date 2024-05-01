




import java.io.PrintWriter;
import java.util.HashSet;


/**
 * A multithreaded chat room server.  When a client connects the
 * server requests a screen name by sending the client the
 * text "SUBMITNAME", and keeps requesting a name until
 * a unique one is received.  After a client submits a unique
 * name, the server acknowledges with "NAMEACCEPTED".  Then
 * all messages from that client will be broadcast to all other
 * clients that have submitted a unique screen name.  The
 * broadcast messages are prefixed with "MESSAGE ".
 * 
 */
public class ChatServerExec {

    private static int CHAT_ROOM_PORT;

    public ChatServerExec(int port) {
        CHAT_ROOM_PORT = port;
    }

    /**
     * Starts an instance of a server in a thread so that GUI thread can continue to operate asynchronously
     */
    public void startServer() {
        ChatServer server = new ChatServer(CHAT_ROOM_PORT); // Create the server instance
        Thread serverThread = new Thread(server); // Correctly pass the server instance to the Thread constructor
        serverThread.start(); // Start the thread, which will invoke ChatServer's run method
    }
}
