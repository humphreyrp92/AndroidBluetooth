package ai.rotor.androidbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

import static ai.rotor.androidbluetooth.RotorUtils.ROTOR_UUID;

public class ConnectThread extends Thread {
    private static final String TAG = "Debug, ConThread";
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;

    public ConnectThread(BluetoothDevice device) {
        Log.d(TAG, "Starting connection thread...");
        BluetoothSocket tmp = null;
        mDevice = device;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try {
            tmp = mDevice.createRfcommSocketToServiceRecord(ROTOR_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }

        mSocket = tmp;
    }

    public void run() {
        mBluetoothAdapter.cancelDiscovery();

        try {
            Log.d(TAG, "trying to connect...");
            mSocket.connect();
        } catch (IOException connectException) {
            try {
                mSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        Log.d(TAG, "Connection attempt succeeded!");

        // manageConnectedThread(socket);

    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
