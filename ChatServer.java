


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Handler;

import javax.swing.JOptionPane;

//TODO STUDENT: edit the class header so that ChatServer can run in a thread
public class ChatServer implements Runnable  { 
    private String name;
    private Socket clientSocket;
    private BufferedReader in;
    private static PrintWriter out;

    private static int CHAT_ROOM_PORT;
    private static final HashSet<String> names = new HashSet<>();
    private static final HashSet<PrintWriter> writers = new HashSet<>();

    public ChatServer(int port) {
        CHAT_ROOM_PORT = port;
    }

    @Override
    public void run() {
        try (ServerSocket listener = new ServerSocket(CHAT_ROOM_PORT)) {
            System.out.println("The chat server is running.");
            while (true) {
                Socket clientSocket = listener.accept(); 
                new Thread(new Handler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler implements Runnable {
        private Socket clientSocket;
        private String name;

        public Handler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null || name.isEmpty()) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        } else {
                            out.println("WRONGNAME");
                        }
                    }
                }

                out.println("NAMEACCEPTED");
                synchronized (writers) {
                    writers.add(out);
                }

                String input;
                while ((input = in.readLine()) != null) {
                    synchronized (writers) {
                        for (PrintWriter writer : writers) {
                            writer.println("MESSAGE " + name + ": " + input);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client #" + name + ": " + e);
            } finally {
                if (name != null) {
                    synchronized (names) {
                        names.remove(name);
                    }
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close a socket, what's going on?");
                }
            }
        }
    }

	 
		private void processClient() throws IOException, InterruptedException {
		    // Initial communication to get a unique name
		    while (true) {
		        out.println("SUBMITNAME");
		        name = in.readLine();
		        if (name == null || name.equals("") || name.equals("null")) continue;
		        synchronized (names) {
		            if (!names.contains(name)) {
		                names.add(name);
		                break;
		            } else {
		                out.println("WRONGNAME");
		                Thread.sleep(100);
		            }
		        }
		    }

		    // Name accepted, proceed to add client's writer to the set
		    out.println("NAMEACCEPTED");
		    writers.add(out);

		    // Start a new thread to handle client messages
		    ServerThreadForClient serverThreadForClient = new ServerThreadForClient(in, out, name);
		    new Thread(serverThreadForClient).start();
		}
		
            
    private class ServerThreadForClient implements Runnable {
    	BufferedReader in;
    	PrintWriter out;
    	String name;
    	
    	ServerThreadForClient (BufferedReader in, PrintWriter out, String name) {
    		this.in =in;
    		this.out =out;
    		this.name= name;
    	}
    	
    	@Override
		public void run() {
            // Accept messages from this client and broadcast them.
            // Ignore other clients that cannot be broadcasted to.
    		try {
                while (true) {
                    String input;
					try {
						input = in.readLine();
					
	                    if (input == null) {
	                        return;
	                    }
	                    for (PrintWriter writer : writers) {
	                        writer.println("MESSAGE " + name + ": " + input);
	                    }
	                } catch (IOException e) {
						e.printStackTrace();
					}
                }
    		}
            finally {
           
            if (name != null) {
                names.remove(name);
            }
            if (out != null) {
                writers.remove(out);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
            }
        }
			
	}
    	
   }
    
}