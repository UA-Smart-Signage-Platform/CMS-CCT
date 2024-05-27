package deti.uas.uasmartsignage.Configuration;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MqttConfig {

    private static IMqttClient instance;

    @Autowired
    private Environment env;

    public IMqttClient getInstance() {
        String serverURI = env.getProperty("mqtt.serverURI");
        String clientId = env.getProperty("mqtt.clientId");
        String username = env.getProperty("mqtt.username");
        String password = env.getProperty("mqtt.password");

        try {
            if (instance == null) {
                instance = new MqttClient(serverURI, clientId);
            }

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);
            options.setConnectionTimeout(10);
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            if (!instance.isConnected()) {
                instance.connect(options);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return instance;
    }

    private MqttConfig() {
    }
}
