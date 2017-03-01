import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient implements Runnable {

    // The client socket
    private static Socket clientSocket = null;
    // The output stream
    private static PrintStream os = null;
    // The input stream
    private static DataInputStream is = null;

    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    public static void main(String[] args) {

        // The default port.
        int portNumber = 	2222;
        // The default host.
        String host = "localhost";

        if (args.length < 2) {
            System.out
                    .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
                            + "Now using host=" + host + ", portNumber=" + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }

    /*
     * Open a socket on a given host and port. Open input and output streams.
     */
            try {


                clientSocket = new Socket(host, portNumber);
                inputLine = new BufferedReader(new InputStreamReader(System.in));
                os = new PrintStream(clientSocket.getOutputStream());
                is = new DataInputStream(clientSocket.getInputStream());
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + host);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to the host "
                        + host);
            }


            if (clientSocket != null && os != null && is != null) {
                try {

        /* Create a thread to read from the server. */
                    new Thread(new ChatClient()).start();
                    while (!closed) {
                        os.println(inputLine.readLine().trim());
                    }

        /*
         * Close the output stream, close the input stream, close the socket.
         */
                    os.close();
                    is.close();
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("IOException:  " + e);
                }
            }
        }



    public void run() {
        String responseLine;
        try {
            while ((responseLine = is.readLine()) != null) {
                System.out.println(responseLine);
                if (responseLine.indexOf("*** Bye") != -1)
                    break;
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}
