package com.pondthaitay.android_mqtt_example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements IMqttActionListener
        , View.OnClickListener, View.OnTouchListener {

    private static final String TOPIC = "20scoopsClawMachine";
    private MqttAndroidClient client;

    @Override
    protected void onStart() {
        super.onStart();
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
        findViewById(R.id.btn_catch).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_up).setOnTouchListener(this);
        findViewById(R.id.btn_down).setOnTouchListener(this);
        findViewById(R.id.btn_right).setOnTouchListener(this);
        findViewById(R.id.btn_left).setOnTouchListener(this);
        setupMQTT();
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Toast.makeText(this, "Connect success!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start:
                sendMessage(true, "6");
                break;
            case R.id.btn_catch:
                sendMessage(true, "5");
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_up:
                sendAction("3", event);
                return true;
            case R.id.btn_down:
                sendAction("4", event);
                return true;
            case R.id.btn_right:
                sendAction("2", event);
                return true;
            case R.id.btn_left:
                sendAction("1", event);
                return true;
            default:
                sendMessage(false, null);
                return true;
        }
    }

    private void setupMQTT() {
        String clientId = MqttClient.generateClientId();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        client = new MqttAndroidClient(this.getApplicationContext(),
                "tcp://broker.hivemq.com:1883",
                clientId);
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(boolean isCancel, String msg) {
        MqttMessage message = new MqttMessage();
        message.setPayload(isCancel ? msg.getBytes() : String.valueOf("0").getBytes());
        message.setQos(1);
        message.setRetained(true);
        try {
            client.publish(TOPIC, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendAction(String action, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            sendMessage(true, action);
        else if (event.getAction() == MotionEvent.ACTION_UP)
            sendMessage(false, null);
    }
}