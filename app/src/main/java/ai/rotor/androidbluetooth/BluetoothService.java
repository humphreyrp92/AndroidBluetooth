package ai.rotor.androidbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static ai.rotor.androidbluetooth.RotorUtils.ROTOR_UUID;

public class BluetoothService {
    private static final String TAG = "Debug, ManConThread: ";
    private ConnectThread mConnectThread;
    private ManageConnectedThread mManageConnectedThread;
    private BluetoothDevice mPairedDevice;
    private BluetoothSocket mSocket;
    private Handler mHandler;
    private Context mContext;

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    public BluetoothService(Context context, BluetoothDevice device) {
        mPairedDevice = device;
        mContext = context;
    }

    public void startClient() {
        mConnectThread = new ConnectThread(mPairedDevice);
        mConnectThread.start();
    }

    /**
     * ConnectThread attempts to connect as a client to a server on the rotor vehicle
     */
    private class ConnectThread extends Thread {
        private static final String TAG = "Debug, ConThread";
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

            manageConnectedThread();

        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private void manageConnectedThread() {
        Log.d(TAG, "Starting ManageConnectedThread...");
        mManageConnectedThread = new ManageConnectedThread();
        mManageConnectedThread.start();
    }


    /**
     * Connected thread handles the connected socket passed by ConnectThread
     */
    private class ManageConnectedThread extends Thread {
        private static final String TAG = "Debug, ManageConThread";
        private final InputStream mInStream;
        private final OutputStream mOutStream;
        private byte[] mBuffer;

        public ManageConnectedThread() {
            Log.d(TAG, "acquiring input and output streams...");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }

            try {
                tmpOut = mSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mInStream = tmpIn;
            mOutStream = tmpOut;
            Log.d(TAG, "Input and output streams acquired!");

            Intent streamsAcquiredIntent = new Intent("streamsAcquired");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(streamsAcquiredIntent);
        }

        public void run() {
            mBuffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mInStream.read(mBuffer);
                    String incomingMessage = new String(mBuffer, 0, bytes);
                    Log.d(TAG, "Input stream: " + incomingMessage);
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "Output stream: " + text);
            try {
                mOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
            }
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    public void write(byte[] bytes) {
        ManageConnectedThread tmp;

        mManageConnectedThread.write(bytes);
    }
}
