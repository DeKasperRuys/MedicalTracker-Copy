package com.carlv.medicaldeliveryrider.Helpers;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private android.os.Handler bluetoothIn;
    final int handlerState = 0;
    Context c;
    public ConnectedThread(BluetoothSocket socket, Handler bluetoothIn, Context c) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.bluetoothIn = bluetoothIn;
        this.c = c;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[256];
        int bytes;
        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                String readMessage = new String(buffer, 0, bytes);
                bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }
    public void write(String input) {
        byte[] msgBuffer = input.getBytes();
        try {
            mmOutStream.write(msgBuffer);
        } catch (IOException e) {
            System.out.println("No paired device");
        }
    }
}
