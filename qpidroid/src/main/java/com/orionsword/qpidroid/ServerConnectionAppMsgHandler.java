package com.orionsword.qpidroid;

import android.os.Handler;
import android.os.Message;

/**
 * Created by pablo on 16/03/14.
 */
public class ServerConnectionAppMsgHandler extends Handler {

    public ServerConnectionAppMsgHandler(ServerConnection app)
    {
        this.app = app;
    }

    @Override
    public void handleMessage(Message msg) {
        app.messageReceived(msg.getData());
    }

    private ServerConnection app;
}
