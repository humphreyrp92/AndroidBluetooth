package ai.rotor.androidbluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ConnectionActivity extends Activity {
    private static final String TAG = "Debug, ConnectionActivity: ";
    private ProgressBar mProgressBar;
    private TextView mStatusTv;
    private BluetoothDevice mPairedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        mProgressBar = (ProgressBar) findViewById(R.id.connectingProgressBar);
        mStatusTv = (TextView) findViewById(R.id.statusTv);

        mPairedDevice = getIntent().getExtras().getParcelable("PairedDevice");

        showConnecting();

        ConnectThread connectThread = new ConnectThread(mPairedDevice);
        connectThread.start();
    }

    private void showConnecting() {
        mProgressBar.setVisibility(View.VISIBLE);
        mStatusTv.setText("Connecting to " + mPairedDevice.getName() + "...");
    }
}
