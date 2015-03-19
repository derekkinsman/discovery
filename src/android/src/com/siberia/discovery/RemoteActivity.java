package com.siberia.discovery;

import android.app.Activity;
import android.content.Intent;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class RemoteActivity extends Activity implements View.OnClickListener {
    private static final String TAG = RemoteActivity.class.getSimpleName();

    private static final int CONNECT_TIMEOUT_MS = 5 * 1000;

    public static final String EXTRA_SERVICE_INFO = "serviceInfo";

    private NsdServiceInfo mServiceInfo;
    private Client mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);

        ImageButton l1 = (ImageButton) findViewById(R.id.remote_button_l1);
        ImageButton l2 = (ImageButton) findViewById(R.id.remote_button_l2);
        ImageButton l3 = (ImageButton) findViewById(R.id.remote_button_l3);
        ImageButton r1 = (ImageButton) findViewById(R.id.remote_button_r1);
        ImageButton r2 = (ImageButton) findViewById(R.id.remote_button_r2);
        ImageButton r3 = (ImageButton) findViewById(R.id.remote_button_r3);
        ImageButton dpadUp = (ImageButton) findViewById(R.id.remote_up);
        ImageButton dpadDown = (ImageButton) findViewById(R.id.remote_down);
        ImageButton dpadLeft = (ImageButton) findViewById(R.id.remote_left);
        ImageButton dpadRight = (ImageButton) findViewById(R.id.remote_right);
        ImageButton buttonO = (ImageButton) findViewById(R.id.remote_button_o);
        ImageButton buttonU = (ImageButton) findViewById(R.id.remote_button_u);
        ImageButton buttonY = (ImageButton) findViewById(R.id.remote_button_y);
        ImageButton buttonA = (ImageButton) findViewById(R.id.remote_button_a);
        ImageButton system = (ImageButton) findViewById(R.id.remote_button_system);
        Button systemMenu = (Button)findViewById(R.id.remote_button_system_menu);

        l1.setOnClickListener(this);
        l2.setOnClickListener(this);
        l3.setOnClickListener(this);
        r1.setOnClickListener(this);
        r2.setOnClickListener(this);
        r3.setOnClickListener(this);
        dpadUp.setOnClickListener(this);
        dpadDown.setOnClickListener(this);
        dpadLeft.setOnClickListener(this);
        dpadRight.setOnClickListener(this);
        buttonO.setOnClickListener(this);
        buttonU.setOnClickListener(this);
        buttonY.setOnClickListener(this);
        buttonA.setOnClickListener(this);
        system.setOnClickListener(this);
        systemMenu.setOnClickListener(this);

        final Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_SERVICE_INFO)) {
            finish();
            return;
        }

        com.esotericsoftware.minlog.Log.set(com.esotericsoftware.minlog.Log.LEVEL_TRACE);
        mServiceInfo = intent.getParcelableExtra(EXTRA_SERVICE_INFO);
        mClient = new Client();
        final Kryo kryo = mClient.getKryo();
        kryo.register(KeyEventMessage.class);
        kryo.register(MotionEventMessage.class);
        kryo.register(SystemMenuMessage.class);
        mClient.addListener(new ClientListener());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mClient.start();
        new Thread(new ConnectRunnable()).start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mClient.isConnected()) {
            mClient.close();
        }
        mClient.stop();
    }

    @Override
    public void onClick(View v) {
        int keyCode = 0;

        switch (v.getId()) {
            case R.id.remote_button_l1:
                keyCode = OuyaController.BUTTON_L1;
                break;
            case R.id.remote_button_l2:
                keyCode = OuyaController.BUTTON_L2;
                break;
            case R.id.remote_button_l3:
                keyCode = OuyaController.BUTTON_L3;
                break;
            case R.id.remote_button_r1:
                keyCode = OuyaController.BUTTON_R1;
                break;
            case R.id.remote_button_r2:
                keyCode = OuyaController.BUTTON_R2;
                break;
            case R.id.remote_button_r3:
                keyCode = OuyaController.BUTTON_R3;
                break;
            case R.id.remote_up:
                keyCode = OuyaController.BUTTON_DPAD_UP;
                break;
            case R.id.remote_down:
                keyCode = OuyaController.BUTTON_DPAD_DOWN;
                break;
            case R.id.remote_left:
                keyCode = OuyaController.BUTTON_DPAD_LEFT;
                break;
            case R.id.remote_right:
                keyCode = OuyaController.BUTTON_DPAD_RIGHT;
                break;
            case R.id.remote_button_o:
                keyCode = OuyaController.BUTTON_O;
                break;
            case R.id.remote_button_u:
                keyCode = OuyaController.BUTTON_U;
                break;
            case R.id.remote_button_y:
                keyCode = OuyaController.BUTTON_Y;
                break;
            case R.id.remote_button_a:
                keyCode = OuyaController.BUTTON_A;
                break;
            case R.id.remote_button_system:
                keyCode = OuyaController.BUTTON_MENU;
                break;
            case R.id.remote_button_system_menu:
                sendMessage(new SystemMenuMessage());
                return;
        }

        sendMessage(new KeyEventMessage(keyCode));
    }

    private void sendMessage(final BaseMessage message) {
        new Thread() {
            @Override
            public void run() {
                mClient.sendTCP(message);
            }
        }.start();
    }

    private class ConnectRunnable implements Runnable {
        @Override
        public void run() {
            try {
                mClient.connect(CONNECT_TIMEOUT_MS, mServiceInfo.getHost(), mServiceInfo.getPort());
            } catch (IOException e) {
                Log.e(TAG, "Unable to connect", e);
                finish();
            }
        }
    }

    private class ClientListener extends Listener {
        @Override
        public void connected(Connection connection) {
            Log.d(TAG, "Connected!");
        }

        @Override
        public void received(Connection connection, Object object) {
            if (!(object instanceof BaseMessage)) {
                return;
            }
            final BaseMessage baseMessage = (BaseMessage) object;
            //switch (baseMessage.getType()) {
            //}
        }
    }
}
