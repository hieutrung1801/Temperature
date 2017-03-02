/**
 * Created by Trung on 3/2/2017.
 */

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Objects;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/*
 * A chat server that delivers public and private messages.
 */
public class TheServer {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String args[]) {

        // The default port number.
        int portNumber = 2222;
        if (args.length < 1) {
            System.out
                    .println("Usage: java MultiThreadChatServer <portNumber>\n"
                            + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

    /*
     * Open a server socket on the portNumber (default 2222).
    */
        try {

            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }

    /*
     * Create a client socket for each connection and pass it to a new client
     * thread.
     */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;

                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        break;
                    }
                }

                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}

/*
 * The chat client thread.
 */
class clientThread extends Thread {

    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        try {
      /*
       * Create input and output streams for this client.
       */
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            os.println("Please choose City from the list : Ho Chi Minh, Newyork, Miami.");
            String name = is.readLine().trim();
            os.println("Now streaming data from " + name
                    + "\nTo stop, enter /quit in a new line");
            //String url = null;

            //else System.out.println("Invalid City");
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].os.println("*** A new user " + name
                            + " entered the chat room !!! ***");
                }
            }
            String url = "http://www.accuweather.com/vi/vn/ho-chi-minh-city/353981/current-weather/353981";
            String answerers = null;
            Document document = Jsoup.connect(url).get();
            if(Objects.equals(name, "Ho Chi Minh")){
                answerers = document.select("[href*=/ho-chi-minh-city/353981/] .temp").text();
            }
            if(Objects.equals(name, "New York")){
                answerers = document.select("[href*=/new-york-ny/10007/] .temp").text();
            }
            if (Objects.equals(name,"Miami")){
                answerers = document.select("[href*=/miami-fl/33128/].temp").text();
            }
                while (true) {
                //if(name == "Ha Noi"){
                //    answerers = document.select().text();
                //}
                String numberOnly = answerers.replaceAll("[^0-9]", "");
                String line = numberOnly;

                if (line.startsWith("/quit")) {
                    break;
                }
                else {
                    String topic = name;
                    String content = line;
                    int qos = 1;
                    if (topic == "Ho Chi Minh"){
                        qos = 1;
                    } else if(topic == "Newyork"){
                        qos = 2;
                    } else if(topic == "Ha noi"){
                        qos = 3;
                    }
                    String broker = "tcp://m10.cloudmqtt.com:10203";

                    //MQTT client id to use for the device. "" will generate a client id automatically
                    String clientId = "1";
                    MemoryPersistence persistence = new MemoryPersistence();
                    try {

                        MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
                        mqttClient.setCallback(new MqttCallback() {
                            public void messageArrived(String topic, MqttMessage msg)
                                    throws Exception {
                                System.out.println("Recived:" + topic);
                                System.out.println("Recived:" + new String(msg.getPayload()));
                            }

                            public void deliveryComplete(IMqttDeliveryToken arg0) {
                                System.out.println("Delivary complete");
                            }

                            public void connectionLost(Throwable arg0) {
                                // TODO Auto-generated method stub
                            }
                        });
                        MqttConnectOptions connOpts = new MqttConnectOptions();
                        connOpts.setCleanSession(true);
                        connOpts.setUserName("rqsiijsp");
                        connOpts.setPassword(new char[]{'w', '3', 'D', 'd', 'H', 'P', 'f','0','w','A','j','4'});
                        mqttClient.connect(connOpts);
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        mqttClient.subscribe(topic, qos);
                        mqttClient.publish(topic, message);
                        mqttClient.disconnect();

                    } catch (MqttException me) {
                        System.out.println("reason " + me.getReasonCode());
                        System.out.println("msg " + me.getMessage());
                        System.out.println("loc " + me.getLocalizedMessage());
                        System.out.println("cause " + me.getCause());
                        System.out.println("excep " + me);
                        me.printStackTrace();

                    }
                }


//                for (int i = 0; i < maxClientsCount; i++) {
//                    if (threads[i] != null) {
//                        threads[i].os.println("<" + name + "> " + line);
//                    }
//                }

            }

            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].os.println("*** The user " + name
                            + " is leaving the chat room !!! ***");
                }
            }

            os.println("*** Bye " + name + " ***");
      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */

            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }

      /*
       * Close the output stream, close the input stream, close the socket.
       */
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}