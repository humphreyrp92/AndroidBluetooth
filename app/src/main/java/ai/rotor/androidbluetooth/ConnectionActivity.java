package ai.rotor.androidbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ConnectionActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "Debug, ConnectionAct";
    private ProgressBar mProgressBar;
    private TextView mStatusTv;
    private Button mFwdBtn, mRevBtn, mRightBtn, mLeftBtn;
    private BluetoothDevice mPairedDevice;
    private BluetoothService mBluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        mProgressBar = (ProgressBar) findViewById(R.id.connectingProgressBar);
        mStatusTv = (TextView) findViewById(R.id.statusTv);
        mFwdBtn = (Button) findViewById(R.id.fwdBtn);
        mRevBtn = (Button) findViewById(R.id.revBtn);
        mLeftBtn = (Button) findViewById(R.id.leftBtn);
        mRightBtn = (Button) findViewById(R.id.rightBtn);

        mPairedDevice = getIntent().getExtras().getParcelable("PairedDevice");

        showConnecting();

        mBluetoothService = new BluetoothService(ConnectionActivity.this, mPairedDevice);
        mBluetoothService.startClient();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("streamsAcquired");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);

        mFwdBtn.setOnClickListener(this);
        mRevBtn.setOnClickListener(this);
        mLeftBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
    }

    private void showConnecting() {
        mProgressBar.setVisibility(View.VISIBLE);
        mStatusTv.setText("Connecting to " + mPairedDevice.getName() + "...");
        mFwdBtn.setEnabled(false);
        mRevBtn.setEnabled(false);
        mLeftBtn.setEnabled(false);
        mRightBtn.setEnabled(false);
    }

    private void showConnected() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mStatusTv.setText("Connected to " + mPairedDevice.getName());
        mFwdBtn.setEnabled(true);
        mRevBtn.setEnabled(true);
        mLeftBtn.setEnabled(true);
        mRightBtn.setEnabled(true);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("streamsAcquired")) {
                showConnected();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fwdBtn: {

                String cmdString = "FWD";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.revBtn: {
                String cmdString = "REV";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.rightBtn: {
                String cmdString = "RIGHT";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.leftBtn: {
                String cmdString = "LEFT";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
        }
    }
}
