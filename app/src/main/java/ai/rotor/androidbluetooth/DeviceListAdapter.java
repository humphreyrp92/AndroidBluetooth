package ai.rotor.androidbluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class DeviceListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<BluetoothDevice> mData;
    private OnPairButtonClickListener mPairListener;
    private OnConnectButtonClickListener mConnectListener;

    public DeviceListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<BluetoothDevice> data) {
        mData = data;
    }

    public void setListener(OnPairButtonClickListener listener) {
        mPairListener = listener;
    }

    public void setListener(OnConnectButtonClickListener listener) {
        mConnectListener = listener;
    }

    public int getCount() {
        return (mData == null) ? 0 : mData.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_device, null);
            holder = new ViewHolder();
            holder.nameTV = (TextView) convertView.findViewById(R.id.tv_name);
            holder.addressTv = (TextView) convertView.findViewById(R.id.tv_address);
            holder.pairBTN = (Button) convertView.findViewById(R.id.btn_pair);
            holder.connectBTN = (Button) convertView.findViewById(R.id.btn_connect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = mData.get(position);

        String name = device.getName();
        if (name != null) {
            holder.nameTV.setText(name);
        } else {
            holder.nameTV.setText("UNKNOWN");
        }
        holder.addressTv.setText(device.getAddress());

        Boolean paired = (device.getBondState() == BluetoothDevice.BOND_BONDED);
        holder.pairBTN.setText(paired ? "Unpair" : "Pair");
        holder.connectBTN.setEnabled(paired ? true : false);
        holder.pairBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPairListener != null) {
                    mPairListener.onPairButtonClick(position);
                }
            }
        });
        holder.connectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnectListener != null) {
                    mConnectListener.onConnectButtonClick(position);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView nameTV;
        TextView addressTv;
        Button pairBTN;
        Button connectBTN;
    }

    public interface OnPairButtonClickListener {
        void onPairButtonClick(int position);
    }

    public interface OnConnectButtonClickListener {
        void onConnectButtonClick(int position);
    }
}
