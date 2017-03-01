/**
 * Created by Trung on 2/28/2017.
 */
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class NYTemp {
    public static void main(String[] args) throws IOException {
        while (true) {
            String topic = "Time"; // cho NY
            String content = "Cho ";
            int qos = 2;
            String broker = "tcp://m10.cloudmqtt.com:10203";
            //MQTT client id to use for the device. "" will generate a client id automatically
            String clientId = "13";
            MemoryPersistence persistence = new MemoryPersistence();
            try {
                MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
                mqttClient.setCallback(new MqttCallback() {
                    public void messageArrived(String topic, MqttMessage msg1)
                            throws Exception {
                        System.out.println("Recived server hcm:" + topic);
                        System.out.println("Recived:" + new String(msg1.getPayload()));
                    }
                    public void deliveryComplete(IMqttDeliveryToken arg0) {
                        System.out.println("Delivary complete for server NY");
                    }

                    public void connectionLost(Throwable arg0) {
                        // TODO Auto-generated method stub
                    }
                });
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                connOpts.setUserName("thanh");
                connOpts.setPassword(new char[]{'1'});
                mqttClient.connect(connOpts);
                String url = "http://www.accuweather.com/vi/vn/ho-chi-minh-city/353981/current-weather/353981";
                Document document = Jsoup.connect(url).get();
                String answerers = document.select("[href*=/new-york-ny/10007/] .temp").text();
                MqttMessage message= new MqttMessage(answerers.getBytes());
                message.setQos(qos);
                System.out.println("Publish message: " + message);
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
    }
}