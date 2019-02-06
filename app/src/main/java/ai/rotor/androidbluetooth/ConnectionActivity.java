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
    private Button mFwdBtn, mFwdLftBtn, mFwdRtBtn, mNeutLftBtn, mNeutBtn, mNeutRtBtn,
        mRevLftBtn, mRevBtn, mRevRtBtn, mTrimFwdBtn, mTrimBkwdBtn, mTrimRtBtn, mTrimLftBtn, mAutoBtn;
    private BluetoothDevice mPairedDevice;
    private BluetoothService mBluetoothService;
    private boolean autoMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        mProgressBar = (ProgressBar) findViewById(R.id.connectingProgressBar);
        mStatusTv = (TextView) findViewById(R.id.statusTv);
        mFwdLftBtn = (Button) findViewById(R.id.fwdLeftBtn);
        mFwdBtn = (Button) findViewById(R.id.fwdBtn);
        mFwdRtBtn = (Button) findViewById(R.id.fwdRightBtn);
        mNeutLftBtn = (Button) findViewById(R.id.leftBtn);
        mNeutBtn = (Button) findViewById(R.id.neutralBtn);
        mNeutRtBtn = (Button) findViewById(R.id.rightBtn);
        mRevLftBtn = (Button) findViewById(R.id.revLeftBtn);
        mRevBtn = (Button) findViewById(R.id.revBtn);
        mRevRtBtn = (Button) findViewById(R.id.revRightBtn);
        mTrimFwdBtn = (Button) findViewById(R.id.trimUpBtn);
        mTrimBkwdBtn = (Button) findViewById(R.id.trimDownBtn);
        mTrimLftBtn = (Button) findViewById(R.id.trimLeftBtn);
        mTrimRtBtn = (Button) findViewById(R.id.trimRightBtn);
        mAutoBtn = (Button) findViewById(R.id.autoBtn);


        mPairedDevice = getIntent().getExtras().getParcelable("PairedDevice");

        showConnecting();

        mBluetoothService = new BluetoothService(ConnectionActivity.this, mPairedDevice);
        mBluetoothService.startClient();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("streamsAcquired");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);

        mFwdLftBtn.setOnClickListener(this);
        mFwdBtn.setOnClickListener(this);
        mFwdRtBtn.setOnClickListener(this);
        mNeutLftBtn.setOnClickListener(this);
        mNeutBtn.setOnClickListener(this);
        mNeutRtBtn.setOnClickListener(this);
        mRevLftBtn.setOnClickListener(this);
        mRevBtn.setOnClickListener(this);
        mRevRtBtn.setOnClickListener(this);
        mTrimFwdBtn.setOnClickListener(this);
        mTrimBkwdBtn.setOnClickListener(this);
        mTrimLftBtn.setOnClickListener(this);
        mTrimRtBtn.setOnClickListener(this);
        mAutoBtn.setOnClickListener(this);

        autoMode = false;
    }

    private void showConnecting() {
        mProgressBar.setVisibility(View.VISIBLE);
        mStatusTv.setText("Connecting to " + mPairedDevice.getName() + "...");
        mFwdLftBtn.setEnabled(false);
        mFwdBtn.setEnabled(false);
        mFwdRtBtn.setEnabled(false);
        mNeutLftBtn.setEnabled(false);
        mNeutBtn.setEnabled(false);
        mNeutRtBtn.setEnabled(false);
        mRevLftBtn.setEnabled(false);
        mRevBtn.setEnabled(false);
        mRevRtBtn.setEnabled(false);
        mTrimFwdBtn.setEnabled(false);
        mTrimBkwdBtn.setEnabled(false);
        mTrimLftBtn.setEnabled(false);
        mTrimRtBtn.setEnabled(false);
        mAutoBtn.setEnabled(false);
    }

    private void showManual() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mStatusTv.setText("Connected to " + mPairedDevice.getName());
        mFwdLftBtn.setEnabled(true);
        mFwdBtn.setEnabled(true);
        mFwdRtBtn.setEnabled(true);
        mNeutLftBtn.setEnabled(true);
        mNeutBtn.setEnabled(true);
        mNeutRtBtn.setEnabled(true);
        mRevLftBtn.setEnabled(true);
        mRevBtn.setEnabled(true);
        mRevRtBtn.setEnabled(true);
        mTrimFwdBtn.setEnabled(true);
        mTrimBkwdBtn.setEnabled(true);
        mTrimLftBtn.setEnabled(true);
        mTrimRtBtn.setEnabled(true);
        mAutoBtn.setEnabled(true);
        mAutoBtn.setText("GO AUTO");
    }

    private void showAutonomous() {
        mFwdLftBtn.setEnabled(false);
        mFwdBtn.setEnabled(false);
        mFwdRtBtn.setEnabled(false);
        mNeutLftBtn.setEnabled(false);
        mNeutBtn.setEnabled(false);
        mNeutRtBtn.setEnabled(false);
        mRevLftBtn.setEnabled(false);
        mRevBtn.setEnabled(false);
        mRevRtBtn.setEnabled(false);
        mTrimFwdBtn.setEnabled(false);
        mTrimBkwdBtn.setEnabled(false);
        mTrimLftBtn.setEnabled(false);
        mTrimRtBtn.setEnabled(false);
        mAutoBtn.setText("GO MANUAL");
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("streamsAcquired")) {
                showManual();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fwdLeftBtn: {
                String cmdString = "F015, L100";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.fwdBtn: {
                String cmdString = "F015, N000";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.fwdRightBtn: {
                String cmdString = "F015, R100";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.leftBtn: {
                String cmdString = "N000, L100";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.neutralBtn: {
                String cmdString = "N000, N000";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.rightBtn: {
                String cmdString = "N000, R100";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.revLeftBtn: {
                String cmdString = "V015, L100";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.revBtn: {
                String cmdString = "V015, N000";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.revRightBtn: {
                String cmdString = "V015, R100";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.trimUpBtn: {
                String cmdString = "TF";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.trimDownBtn: {
                String cmdString = "TV";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.trimLeftBtn: {
                String cmdString = "TL";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.trimRightBtn: {
                String cmdString = "TR";
                mBluetoothService.write(cmdString.getBytes());
                Log.d(TAG, "outgoing message: " + cmdString);
                break;
            }
            case R.id.autoBtn: {
                if (autoMode) {
                    String cmdString = "_M";
                    mBluetoothService.write(cmdString.getBytes());
                    Log.d(TAG, "outgoing message: " + cmdString);
                    autoMode = false;
                    showManual();
                    break;
                } else {
                    String cmdString = "_A";
                    mBluetoothService.write(cmdString.getBytes());
                    Log.d(TAG, "outgoing message: " + cmdString);
                    autoMode = true;
                    showAutonomous();
                    break;
                }
            }
        }
    }
}
