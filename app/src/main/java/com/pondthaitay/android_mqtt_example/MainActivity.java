package com.pondthaitay.android_mqtt_example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements IMqttActionListener {

    private static final String TOPIC = "20scoopsClawMachine";
    private static final String SERVER_URI = "tcp://broker.hivemq.com:1883";
    private MqttAndroidClient client;

    @Override
    protected void onStart() {
        super.onStart();
        setupMQTT();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (client != null) client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggle);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> sendMessage(isChecked));
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Toast.makeText(this, "Connect success!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setupMQTT() {
        String clientId = MqttClient.generateClientId();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        client = new MqttAndroidClient(this.getApplicationContext(), SERVER_URI, clientId);
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(boolean isChecked) {
        MqttMessage message = new MqttMessage();
        message.setPayload(getActionNumber(isChecked));
        message.setQos(1);
        message.setRetained(true);
        try {
            client.publish(TOPIC, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private byte[] getActionNumber(boolean isChecked) {
        Random random = new Random();
        return isChecked ? String.valueOf(random.nextInt(5) + 1).getBytes() :
                String.valueOf("0").getBytes();
    }
}