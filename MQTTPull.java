import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.awt.*;
import java.lang.String;
import java.util.*;
import java.util.List;

public class MQTTPull implements MqttCallback {
    public MQTTPull() {
    }

    public static void main(String[] args) {
        new MQTTPull().doDemo();

    }
    public void doDemo() {
        try {
            String broker       = "tcp://m10.cloudmqtt.com:10203";

            //MQTT client id to use for the device. "" will generate a client id automatically
            String clientId     = "1";
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName("rqsiijsp");
            connOpts.setPassword(new char[]{'w', '3', 'D', 'd', 'H', 'P', 'f','0','w','A','j','4'});

            /*Pulling*/
            mqttClient.connect(connOpts);
            mqttClient.setCallback(this);
            mqttClient.subscribe("Ho Chi Minh", 1);
            mqttClient.subscribe("Newyork", 2);
            //mqttClient.subscribe("Ha Noi", 3);
            MqttMessage message = new MqttMessage();
            message.setPayload("This is the temperature".getBytes());
            //mqttClient.publish("Time", message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        // TODO Auto-generated method stub

    }
    List<Integer> mylist = new ArrayList<Integer>();
    @Override

    public void messageArrived(String topic, MqttMessage message)
            throws Exception {
        float average =0;
        int count = 0;
        //String[] tok = message.split("\u00B0");
        //String result = Arrays.toString(tok);
        //String str="sdfvsdf68fsdfsf8999fsdf09";
        String numberOnly= message.toString().replaceAll("[^0-9]", "");
        mylist.add(Integer.parseInt(String.valueOf(numberOnly)));
        System.out.println(message);
        for (int i =0; i< mylist.size(); i++){
            average += mylist.get(i);
            count ++;
        }
        System.out.println("Average: "+ average/count);
        System.out.println("Time is: " + new Date());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub

    }

}
