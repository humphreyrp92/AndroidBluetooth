package ai.rotor.androidbluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Debug, MainActivity:";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private TextView mTvStatus;
    private Button mBtnActivate;
    private Button mBtnPaired;
    private Button mBtnScan;
    private ProgressBar mScanProgressBar;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvStatus = (TextView) findViewById(R.id.tvStatus);
        mBtnActivate = (Button) findViewById(R.id.btnActivate);
        mBtnPaired = (Button) findViewById(R.id.btnPaired);
        mBtnScan = (Button) findViewById(R.id.btnScan);
        mScanProgressBar = (ProgressBar) findViewById(R.id.scanProgressBar);
        mScanProgressBar.setVisibility(View.INVISIBLE);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.setName("Shield Tablet");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);




        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            mBtnPaired.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                    if (pairedDevices == null || pairedDevices.size() == 0) {
                        showToast("No paired devices found.");
                    } else {
                        ArrayList<BluetoothDevice> list = new ArrayList<>();
                        list.addAll(pairedDevices);
                        Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
                        intent.putParcelableArrayListExtra("device.list", list);
                        startActivity(intent);
                    }
                }
            });

            mBtnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBluetoothAdapter.startDiscovery();
                }
            });

            mBtnActivate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();
                        showDisabled();
                    } else {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, 1000);
                    }
                }
            });

            if (mBluetoothAdapter.isEnabled()) {
                Log.d(TAG, "showing enabled");
                showEnabled();
            } else {
                showDisabled();
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        registerReceiver(mReceiver, filter);
    }

    private void showUnsupported() {
        mTvStatus.setText("Bluetooth is not supported on this device");

        mBtnActivate.setText("Enable");
        mBtnActivate.setEnabled(false);

        mBtnPaired.setEnabled(false);
        mBtnScan.setEnabled(false);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showDisabled() {
        showToast("Disabled");
        mTvStatus.setText("Bluetooth is OFF");
        mTvStatus.setTextColor(Color.RED);

        mBtnActivate.setText("Enable");
        mBtnActivate.setEnabled(true);

        mBtnPaired.setEnabled(false);
        mBtnScan.setEnabled(false);
    }

    private void showEnabled() {
        mTvStatus.setText("Bluetooth is ON");
        mTvStatus.setTextColor(Color.BLUE);

        mBtnActivate.setText("Disable");
        mBtnActivate.setEnabled(true);

        mBtnPaired.setEnabled(true);
        mBtnScan.setEnabled(true);
    }

    private void showPaired() {

    }

    private void showConnected() {

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private static final String TAG = "Debug";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    showToast("Enabled");
                    showEnabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<>();
                mScanProgressBar.setVisibility(View.VISIBLE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mScanProgressBar.setVisibility(View.INVISIBLE);
                Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                newIntent.putParcelableArrayListExtra("device.list", mDeviceList);
                startActivity(newIntent);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Found Bluetooth device at address " + device.getAddress());
                mDeviceList.add(device);
            }
        }
    };
}
