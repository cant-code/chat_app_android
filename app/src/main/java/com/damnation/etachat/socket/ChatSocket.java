package com.damnation.etachat.socket;

import android.app.Application;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class ChatSocket extends Application {
    private static Socket mSocket = null;

    public static Socket getmSocket() {
        if(mSocket == null) {
            try {
                mSocket = IO.socket("https://etachat.herokuapp.com/");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return mSocket;
    }
}
