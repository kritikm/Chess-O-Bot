package com.first.kritikm.chessobot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button leftForward, leftBackward, rightForward, rightBackward;
    private BluetoothDevice arduino;
    private BluetoothSocket arduinoSocket;
    private OutputStream arduinoOutput;
    TextView testText;
    RadioGroup speedGroup;
    RadioButton speed;
    UUID uuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        arduino = null;

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0)
        {
            Log.d("Paired Devices", String.valueOf(pairedDevices.size()));
            for(BluetoothDevice device : pairedDevices)
            {
                Log.d("Device Parsing", device.getName());
                if(device.getName().equals("KHA"))
                {
                    arduino = device;
                    Log.d("Arduino", arduino.getName().toString());
                    break;
                }
            }
        }

        uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {
            if(arduino != null) {
                arduinoSocket = arduino.createRfcommSocketToServiceRecord(uuid);
                if(arduinoSocket == null)
                    Log.d("Socket", "Null");
                else
                {
                    Log.d("Socket", "Made");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                arduinoSocket.connect();
                            }
                            catch (IOException ioexec)
                            {
                                Log.d("Socket", ioexec.toString());
                            }

                            Log.d("Socket", "Connect executed");
                            final TextView connStatus = (TextView) findViewById(R.id.connStatus);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(arduinoSocket.isConnected())
                                    {
                                        try {
                                            Log.d("Socket", "Connected");
                                            arduinoOutput = arduinoSocket.getOutputStream();
//                                    try
//                                    {
//                                        arduinoOutput.write('#');
//                                    }
//                                    catch (IOException ioe)
//                                    {
//                                        Log.d("Connection", "Initiated");
//                                    }
                                            connStatus.setBackgroundColor(Color.parseColor("#90ee90"));
                                        }
                                        catch (IOException ex)
                                        {
                                            Log.d("Socket", "Output Stream Null");
                                        }
                                    }
                                    else {
                                        Log.d("Socket", "Not Connected");
                                        connStatus.setBackgroundColor(Color.parseColor("#ff0000"));
                                    }


                                }
                            });
                        }
                    }).start();
                }
            }
        }
        catch (Exception ex)
        {
            Log.d("uninitialised", "cannot connect to arduino");
        }




        leftForward = (Button)findViewById(R.id.leftForward);
        leftBackward = (Button)findViewById(R.id.leftBackward);
        rightForward = (Button)findViewById(R.id.rightForward);
        rightBackward = (Button)findViewById(R.id.rightBackward);
        testText = (TextView)findViewById(R.id.testText);
        speedGroup = (RadioGroup)findViewById(R.id.speedGroup);
        final char[] checker = {'0', '0', '0', '0', '0'};
        //CHECKER{LEFT FWD, LEFT BCK, RIGHT FWD, RIGHT BCK, SPEED(0,1,2)}
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if(arduinoOutput != null) {
                    String direction = String.valueOf(checker).substring(0, 4);

                    switch (direction) {
                        //Speed Low
                        case "1000":

                            Log.d("Send", "Left Forward");
                            switch (checker[4]) {
                                case '0':
                                    Log.d("Speed", "Low");
                                    //Low speed Forward Code
                                    try {
                                        arduinoOutput.write('A');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }

                                    break;

                                case '1':
                                    Log.d("Speed", "Mid");
                                    try {
                                        arduinoOutput.write('J');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;

                                case '2':
                                    Log.d("Speed", "High");
                                    try {
                                        arduinoOutput.write('S');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;
                            }

                            break;

                        case "0100":

                            Log.d("Send", "Left Backward");
                            switch (checker[4]) {
                                case '0':
                                    Log.d("Speed", "Low");
                                    //Low speed Forward Code
                                    try {
                                        arduinoOutput.write('B');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }

                                    break;

                                case '1':
                                    Log.d("Speed", "Mid");
                                    try {
                                        arduinoOutput.write('K');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;

                                case '2':
                                    Log.d("Speed", "High");
                                    try {
                                        arduinoOutput.write('T');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;
                            }

                            break;

                        case "1010":
                            Log.d("Send", "Left Forward Right Forward");
                            switch (checker[4]) {
                                case '0':
                                    Log.d("Speed", "Low");
                                    //Low speed Forward Code
                                    try {
                                        arduinoOutput.write('C');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }

                                    break;

                                case '1':
                                    Log.d("Speed", "Mid");
                                    try {
                                        arduinoOutput.write('L');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;

                                case '2':
                                    Log.d("Speed", "High");
                                    try {
                                        arduinoOutput.write('U');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;
                            }

                            break;

                        case "1001":
                            Log.d("Send", "Left Forward Right Backward");
                            switch (checker[4]) {
                                case '0':
                                    Log.d("Speed", "Low");
                                    //Low speed Forward Code
                                    try {
                                        arduinoOutput.write('D');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }

                                    break;

                                case '1':
                                    Log.d("Speed", "Mid");
                                    try {
                                        arduinoOutput.write('M');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;

                                case '2':
                                    Log.d("Speed", "High");
                                    try {
                                        arduinoOutput.write('V');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;
                            }

                            break;

                        case "0110":
                            Log.d("Send", "Left Backward Right Forward");
                            switch (checker[4]) {
                                case '0':
                                    Log.d("Speed", "Low");
                                    //Low speed Forward Code
                                    try {
                                        arduinoOutput.write('E');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }

                                    break;

                                case '1':
                                    Log.d("Speed", "Mid");
                                    try {
                                        arduinoOutput.write('N');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;

                                case '2':
                                    Log.d("Speed", "High");
                                    try {
                                        arduinoOutput.write('W');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;
                            }

                            break;

                        case "0101":
                            Log.d("Send", "Left Backward Right Backward");
                            switch (checker[4]) {
                                case '0':
                                    Log.d("Speed", "Low");
                                    //Low speed Forward Code
                                    try {
                                        arduinoOutput.write('F');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }

                                    break;

                                case '1':
                                    Log.d("Speed", "Mid");
                                    try {
                                        arduinoOutput.write('O');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;

                                case '2':
                                    Log.d("Speed", "High");
                                    try {
                                        arduinoOutput.write('X');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;
                            }

                            break;

                        case "0010":
                            Log.d("Send", "Right Forward");
                            switch (checker[4]) {
                                case '0':
                                    Log.d("Speed", "Low");
                                    //Low speed Forward Code
                                    try {
                                        arduinoOutput.write('G');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }

                                    break;

                                case '1':
                                    Log.d("Speed", "Mid");
                                    try {
                                        arduinoOutput.write('P');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;

                                case '2':
                                    Log.d("Speed", "High");
                                    try {
                                        arduinoOutput.write('Y');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;
                            }

                            break;

                        case "0001":
                            Log.d("Send", "Right Backward");
                            switch (checker[4]) {
                                case '0':
                                    Log.d("Speed", "Low");
                                    //Low speed Forward Code
                                    try {
                                        arduinoOutput.write('H');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }

                                    break;

                                case '1':
                                    Log.d("Speed", "Mid");
                                    try {
                                        arduinoOutput.write('Q');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;

                                case '2':
                                    Log.d("Speed", "High");
                                    try {
                                        arduinoOutput.write('Z');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;
                            }

                            break;

                        default:
                            Log.d("Send", "Equilibrium");
                            switch (checker[4]) {
                                case '0':
                                    Log.d("Speed", "Low");
                                    //Low speed Forward Code
                                    try {
                                        arduinoOutput.write('I');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }

                                    break;

                                case '1':
                                    Log.d("Speed", "Mid");
                                    try {
                                        arduinoOutput.write('R');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;

                                case '2':
                                    Log.d("Speed", "High");
                                    try {
                                        arduinoOutput.write('[');
                                    } catch (IOException ex) {
                                        Log.d("WriteException", ex.toString());
                                    }
                                    break;
                            }
                            break;
                    }
                }
            }
        }, 0, 100);//put here time 1000 milliseconds=1 second

        leftForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                speed = (RadioButton)findViewById(speedGroup.getCheckedRadioButtonId());

                switch (event.getAction())
                {

                    case MotionEvent.ACTION_DOWN:
                        checker[0] = '1';
                        switch (speed.getText().toString())
                        {
                            case "Low":
                                checker[4] = '0';
                                break;

                            case "Mid":
                                checker[4] = '1';
                                break;

                            case "High":
                                checker[4] = '2';
                                break;
                        }
                        testText.setText(String.valueOf(checker));
                        break;

                    case MotionEvent.ACTION_UP:
                        checker[0] = '0';
                        switch (speed.getText().toString())
                        {
                            case "Low":
                                checker[4] = '0';
                                break;

                            case "Mid":
                                checker[4] = '1';
                                break;

                            case "High":
                                checker[4] = '2';
                                break;
                        }

                        testText.setText(String.valueOf(checker));
                        break;
                }
                return false;
            }
        });

        leftBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                speed = (RadioButton)findViewById(speedGroup.getCheckedRadioButtonId());
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        checker[1] = '1';
                        switch (speed.getText().toString())
                        {
                            case "Low":
                                checker[4] = '0';
                                break;

                            case "Mid":
                                checker[4] = '1';
                                break;

                            case "High":
                                checker[4] = '2';
                                break;
                        }

                        testText.setText(String.valueOf(checker));
                        break;

                    case MotionEvent.ACTION_UP:
                        checker[1] = '0';
                        switch (speed.getText().toString())
                        {
                            case "Low":
                                checker[4] = '0';
                                break;

                            case "Mid":
                                checker[4] = '1';
                                break;

                            case "High":
                                checker[4] = '2';
                                break;
                        }

                        testText.setText(String.valueOf(checker));
                        break;
                }
                return false;
            }
        });

        rightForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                speed = (RadioButton)findViewById(speedGroup.getCheckedRadioButtonId());

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        checker[2] = '1';
                        switch (speed.getText().toString())
                        {
                            case "Low":
                                checker[4] = '0';
                                break;

                            case "Mid":
                                checker[4] = '1';
                                break;

                            case "High":
                                checker[4] = '2';
                                break;
                        }

                        testText.setText(String.valueOf(checker));
                        break;

                    case MotionEvent.ACTION_UP:
                        checker[2] = '0';
                        switch (speed.getText().toString())
                        {
                            case "Low":
                                checker[4] = '0';
                                break;

                            case "Mid":
                                checker[4] = '1';
                                break;

                            case "High":
                                checker[4] = '2';
                                break;
                        }

                        testText.setText(String.valueOf(checker));
                        break;
                }
                return false;
            }
        });

        rightBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                speed = (RadioButton)findViewById(speedGroup.getCheckedRadioButtonId());
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        checker[3] = '1';
                        switch (speed.getText().toString())
                        {
                            case "Low":
                                checker[4] = '0';
                                break;

                            case "Mid":
                                checker[4] = '1';
                                break;

                            case "High":
                                checker[4] = '2';
                                break;
                        }

                        testText.setText(String.valueOf(checker));
                        break;

                    case MotionEvent.ACTION_UP:
                        checker[3] = '0';
                        switch (speed.getText().toString())
                        {
                            case "Low":
                                checker[4] = '0';
                                break;

                            case "Mid":
                                checker[4] = '1';
                                break;

                            case "High":
                                checker[4] = '2';
                                break;
                        }

                        testText.setText(String.valueOf(checker));
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try
        {
            arduinoOutput.write('!');
        }
        catch (IOException io)
        {
            Log.d("Connection", "Closed");
        }
    }
}
