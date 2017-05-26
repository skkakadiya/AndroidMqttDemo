package com.example.sanjay.mqttdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    MqttClient client = null;
    private boolean state = true;
    Button btnState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnState = (Button) findViewById(R.id.btn_send);
        String msg = state ? "OFF" : "ON";
        btnState.setText(msg);
        try {
            client = new MqttClient("tcp://iot.eclipse.org:1883", "android", new MemoryPersistence());
            client.connect();

            subcribe();


            btnState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        String msg = state ? "OFF" : "ON";
                        state = !state;
                        btnState.setText(msg);
                        MqttMessage message = new MqttMessage();
                        message.setPayload(msg.getBytes());
                        client.publish("testauber", message);
                        Log.i(TAG, "onClick: "+ msg);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }


                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subcribe(){
        try {
            client.subscribe("testauber");
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    try {
                        client = new MqttClient("tcp://iot.eclipse.org:1883", "android", new MemoryPersistence());
                        client.connect();
                        subcribe();
                        Log.e(TAG, "connectionLost");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String srt = new String(mqttMessage.getPayload());
                    Log.e(TAG, "messageArrived: " + srt);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
