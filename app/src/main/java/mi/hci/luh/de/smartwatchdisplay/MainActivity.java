package mi.hci.luh.de.smartwatchdisplay;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Set;

import static android.R.id.list;
import static android.content.ContentValues.TAG;
import static com.google.android.gms.wearable.CapabilityApi.FILTER_REACHABLE;
import static mi.hci.luh.de.smartwatchdisplay.BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED;

public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_ENABLE_BT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txt_output = (TextView) findViewById(R.id.output);

        BluetoothManager btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

        BluetoothAdapter btAdapter = btManager.getAdapter();
        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }

//        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
//        BluetoothGatt mBluetoothGatt;
//
//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice bt : pairedDevices) {
//                txt_output.setText(bt.getName() + " - " + bt.getAddress());
//                mBluetoothGatt = bt.connectGatt(this, false, btleGattCallback);
//                mBluetoothGatt.discoverServices();
//            }
//        } else {
//            Log.d("msg","No Paired Bluetooth Devices Found.");
//        }

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Wearable.API)
                .build();

        PendingResult result;
        result = Wearable.CapabilityApi.getAllCapabilities(mGoogleApiClient, FILTER_REACHABLE);

        //Wearable.ChannelApi.openChannel(mGoogleApiClient,)
    }



    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a 			BluetoothGatt.discoverServices() call
        }
    };

    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.w(TAG, "onServicesDiscovered received: " + status);
        } else {
            Log.w(TAG, "onServicesDiscovered received: " + status);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // The Wearable API is unavailable
            Log.d("Error","Smartwatch not available");
        }
    }
}


