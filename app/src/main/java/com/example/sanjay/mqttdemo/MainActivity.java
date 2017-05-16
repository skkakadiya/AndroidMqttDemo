package com.example.sanjay.mqttdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private TextView tvIncoming;
    MqttClient client = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvIncoming = (TextView) findViewById(R.id.tv_incoming);

        try {
             //client = new MqttClient("tcp://192.168.43.145:1883", "android1233", new MemoryPersistence());
            client = new MqttClient("tcp://iot.eclipse.org:1883", "android", new MemoryPersistence());
            client.connect();
            /*MqttMessage message = new MqttMessage();
            message.setPayload("A single message".getBytes());
            client.publish("aubertest", message);*/
            //client.disconnect();

            subcribe();


            findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        EditText et = (EditText) findViewById(R.id.et_msg);
                        String msg = et.getText().toString();
                        MqttMessage message = new MqttMessage();
                        message.setPayload(msg.getBytes());
                        client.publish("aubertest", message);
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
            client.subscribe("aubertest");
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    try {
                        //client = new MqttClient("tcp://192.168.43.145:1883", "android", new MemoryPersistence());
                        client = new MqttClient("tcp://iot.eclipse.org:1883", "android", new MemoryPersistence());
                        client.connect();
                        subcribe();
                        Log.e(TAG, "connectionLost");
                        //client.reconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    //client = new MqttClient("tcp://iot.eclipse.org:1883", "android", new MemoryPersistence());
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String srt = new String(mqttMessage.getPayload());
                    tvIncoming.setText(srt);
                    Log.e(TAG, "messageArrived: " + s);
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
