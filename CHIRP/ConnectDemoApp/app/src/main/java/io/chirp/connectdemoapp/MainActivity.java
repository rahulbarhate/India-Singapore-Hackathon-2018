package io.chirp.connectdemoapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;

import io.chirp.connect.ChirpConnect;
import io.chirp.connect.interfaces.ConnectEventListener;
import io.chirp.connect.interfaces.ConnectSetConfigListener;
import io.chirp.connect.models.ChirpError;
import io.chirp.connect.models.ConnectState;


public class MainActivity extends AppCompatActivity {

    private ChirpConnect chirpConnect;

    private static final int RESULT_REQUEST_RECORD_AUDIO = 1;

    TextView status;
    TextView lastChirp;
    TextView versionView;

    Button startStopSdkBtn;
    Button startStopSendingBtn;

    Boolean startStopSdkBtnPressed = false;

    String APP_KEY = "";
    String APP_SECRET = "";
    String APP_CONFIG = "";

    String TAG = "ConnectDemoApp";

    View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLayout = findViewById(android.R.id.content);

        status = (TextView) findViewById(R.id.stateValue);
        lastChirp = (TextView) findViewById(R.id.lastChirp);
        versionView = (TextView) findViewById(R.id.versionView);
        startStopSdkBtn = (Button) findViewById(R.id.startStopSdkBtn);
        startStopSendingBtn = (Button) findViewById(R.id.startStopSengingBtn);

        startStopSendingBtn.setAlpha(.4f);
        startStopSendingBtn.setClickable(false);
        startStopSdkBtn.setAlpha(.4f);
        startStopSdkBtn.setClickable(false);

        Log.v("Connect Version: ", ChirpConnect.getVersion());
        versionView.setText(ChirpConnect.getVersion());

        if (APP_KEY.equals("") || APP_SECRET.equals("")) {
            Log.e(TAG, "APP_KEY or APP_SECRET is not set. " +
                    "Please update with your APP_KEY/APP_SECRET from admin.chirp.io");
            return;
        }
        
        /**
         * Key and secret initialisation
         */
        chirpConnect = new ChirpConnect(this, APP_KEY, APP_SECRET);
        chirpConnect.setConfig(APP_CONFIG, new ConnectSetConfigListener() {

            @Override
            public void onSuccess() {

                //Set-up the connect callbacks
                chirpConnect.setListener(connectEventListener);
                //Enable Start/Stop button
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startStopSdkBtn.setAlpha(1f);
                        startStopSdkBtn.setClickable(true);
                    }
                });
            }

            @Override
            public void onError(ChirpError setConfigError) {
                Log.e("setConfigError", setConfigError.getMessage());
            }
        });

    }


    ConnectEventListener connectEventListener = new ConnectEventListener() {

        @Override
        public void onSending(byte[] data, byte channel) {
            /**
             * onSending is called when a send event begins.
             * The data argument contains the payload being sent.
             */
            String hexData = "null";
            if (data != null) {
                hexData = chirpConnect.payloadToHexString(data);
            }
            Log.v("connectdemoapp", "ConnectCallback: onSending: " + hexData + " on channel: " + channel);
            updateLastPayload(hexData);
        }

        @Override
        public void onSent(byte[] data, byte channel) {
            /**
             * onSent is called when a send event has completed.
             * The data argument contains the payload that was sent.
             */
            String hexData = "null";
            if (data != null) {
                hexData = chirpConnect.payloadToHexString(data);
            }
            updateLastPayload(hexData);
            Log.v("connectdemoapp", "ConnectCallback: onSent: " + hexData + " on channel: " + channel);
        }

        @Override
        public void onReceiving(byte channel) {
            /**
             * onReceiving is called when a receive event begins.
             * No data has yet been received.
             */
            Log.v("connectdemoapp", "ConnectCallback: onReceiving on channel: " + channel);
        }

        @Override
        public void onReceived(byte[] data, byte channel) {
            /**
             * onReceived is called when a receive event has completed.
             * If the payload was decoded successfully, it is passed in data.
             * Otherwise, data is null.
             */
            String hexData = "null";
            if (data != null) {
                hexData = chirpConnect.payloadToHexString(data);
            }
            Log.v("connectdemoapp", "ConnectCallback: onReceived: " + hexData + " on channel: " + channel);
            updateLastPayload(hexData);
        }

        @Override
        public void onStateChanged(byte oldState, byte newState) {
            /**
             * onStateChanged is called when the SDK changes state.
             */
            ConnectState state = ConnectState.createConnectState(newState);
            Log.v("connectdemoapp", "ConnectCallback: onStateChanged " + oldState + " -> " + newState);
            if (state == ConnectState.ConnectNotCreated) {
                updateStatus("NotCreated");
            } else if (state == ConnectState.AudioStateStopped) {
                updateStatus("Stopped");
            } else if (state == ConnectState.AudioStatePaused) {
                updateStatus("Paused");
            } else if (state == ConnectState.AudioStateRunning) {
                updateStatus("Running");
            } else if (state == ConnectState.AudioStateSending) {
                updateStatus("Sending");
            } else if (state == ConnectState.AudioStateReceiving) {
                updateStatus("Receiving");
            } else {
                updateStatus(newState + "");
            }

        }

        @Override
        public void onSystemVolumeChanged(int oldVolume, int newVolume) {
            /**
             * onSystemVolumeChanged is called when the system volume is changed.
             */
            Snackbar snackbar = Snackbar.make(parentLayout, "System volume has been changed to: " + newVolume, Snackbar.LENGTH_LONG);
            snackbar.setAction("CLOSE", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
            Log.v("connectdemoapp", "System volume has been changed, notify user to increase the volume when sending data");
        }

    };

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
        }
        else {
            if (startStopSdkBtnPressed) startSdk();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (startStopSdkBtnPressed) stopSdk();
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        chirpConnect.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            chirpConnect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        stopSdk();
    }

    public void updateStatus(final String newStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText(newStatus);
            }
        });
    }
    public void updateLastPayload(final String newPayload) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lastChirp.setText(newPayload);
            }
        });
    }

    public void stopSdk() {
        ChirpError error = chirpConnect.stop();
        if (error.getCode() > 0) {
            Log.e("ConnectError: ", error.getMessage());
            return;
        }
        startStopSendingBtn.setAlpha(.4f);
        startStopSendingBtn.setClickable(false);
        startStopSdkBtn.setText("Start Sdk");
    }

    public void startSdk() {
        ChirpError error = chirpConnect.start();
        if (error.getCode() > 0) {
            Log.e("ConnectError: ", error.getMessage());
            return;
        }
        startStopSendingBtn.setAlpha(1f);
        startStopSendingBtn.setClickable(true);
        startStopSdkBtn.setText("Stop Sdk");
    }

    public void startStopSdk(View view) {
        /**
         * Start or stop the SDK.
         * Audio is only processed when the SDK is running.
         */
        startStopSdkBtnPressed = true;
        if (chirpConnect.getConnectState() == ConnectState.AudioStateStopped) {
            startSdk();
        } else {
            stopSdk();
        }
    }

    public void sendPayload(View view) {
    	/**
         * A payload is a byte array dynamic size with a maximum size defined by the config string.
         *
         * Generate a random payload, and send it.
         */
    	long maxPayloadLength = chirpConnect.getMaxPayloadLength();
    	long size = (long) new Random().nextInt((int) maxPayloadLength) + 1;
        byte[] payload = chirpConnect.randomPayload(size);
        long maxSize = chirpConnect.getMaxPayloadLength();
        if (maxSize < payload.length) {
            Log.e("ConnectError: ", "Invalid Payload");
            return;
        }
        ChirpError error = chirpConnect.send(payload);
        if (error.getCode() > 0) {
            Log.e("ConnectError: ", error.getMessage());
        }
    }

}
