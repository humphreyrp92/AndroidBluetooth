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
    private OnPairButtonClickListener mListener;

    public DeviceListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<BluetoothDevice> data) {
        mData = data;
    }

    public void setListener(OnPairButtonClickListener listener) {
        mListener = listener;
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
        holder.pairBTN.setText((device.getBondState() == BluetoothDevice.BOND_BONDED) ? "Unpair" : "Pair");
        holder.pairBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPairButtonClick(position);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView nameTV;
        TextView addressTv;
        TextView pairBTN;
    }

    public interface OnPairButtonClickListener {
        void onPairButtonClick(int position);
    }
}
