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
            String clientId1     = "4";
            String clientId2     = "2";
            String clientId3     = "3";
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient mqttClient1 = new MqttClient(broker, clientId1, persistence);
            MqttClient mqttClient2 = new MqttClient(broker, clientId2, persistence);
            MqttClient mqttClient3 = new MqttClient(broker, clientId3, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName("rqsiijsp");
            connOpts.setPassword(new char[]{'w', '3', 'D', 'd', 'H', 'P', 'f','0','w','A','j','4'});

            /*Pulling*/
            mqttClient1.connect(connOpts);
            mqttClient1.setCallback(this);
            //mqttClient2.connect(connOpts);
            //mqttClient2.setCallback(this);
            //mqttClient3.connect(connOpts);
            //mqttClient3.setCallback(this);
            mqttClient1.subscribe("Ho Chi Minh");
            mqttClient1.subscribe("New York");
            mqttClient1.subscribe("Miami");
            //mqttClient2.subscribe("New York");
            //mqttClient3.subscribe("Miami");
            MqttMessage message = new MqttMessage();
            message.setPayload("".getBytes());
//            mqttClient1.publish("Ho Chi Minh", message);
//            mqttClient2.publish("New York", message);
//            mqttClient3.publish("Miami", message);
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
        String numberOnly= message.toString().replaceAll("[^0-9]", "");
        mylist.add(Integer.parseInt(String.valueOf(numberOnly)));
        System.out.println(message);
        for (int i =0; i< mylist.size(); i++){
            average += mylist.get(i);
            count ++;
        }
        System.out.println("pulled from: "+ topic);
        System.out.println("Average: "+ average/count);
        System.out.println("Time is: " + new Date());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub

    }

}
