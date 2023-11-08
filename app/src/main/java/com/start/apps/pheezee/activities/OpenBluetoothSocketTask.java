package com.start.apps.pheezee.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;

import java.io.IOException;
import java.util.UUID;

// Asynchronous task to open a Bluetooth socket
public class OpenBluetoothSocketTask extends AsyncTask<Void, Void, Boolean> {
    private String deviceAddress;
    private UUID uuid;
    private BluetoothSocket socket;
    private SocketConnectionListener listener;

    public OpenBluetoothSocketTask(String deviceAddress, UUID uuid, SocketConnectionListener listener) {
        this.deviceAddress = deviceAddress;
        this.uuid = uuid;
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected Boolean doInBackground(Void... params) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            if (device != null) {
                try {
                    socket = device.createRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    socket.close();
//                    BluetoothPrintersConnections.selectFirstPaired();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean isConnected) {
        if (listener != null) {
            listener.onSocketConnectionResult(isConnected, socket);
        }
    }

    public interface SocketConnectionListener {
        void onSocketConnectionResult(boolean isConnected, BluetoothSocket socket);
    }
}
