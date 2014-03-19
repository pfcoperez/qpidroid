package com.orionsword.qpidroid;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pablo on 31/12/13.
 */
public class QpidroidProcess extends Service {

    public QpidroidProcess() {
        super();

        msgHandler = new FromClientHandler();
        toMe = new Messenger(msgHandler);
        toClient = null;
        messageSimulator = new Timer();
    }

    /*
        Whenever a client creates and raises and intent asking for this services,
        this method will be called and the service will generate a binder so clients can send
        messages to the service.
         */
    @Override
    public IBinder onBind(Intent intent)
    {
        //System.out.println("Service: onBind");
        doToast("Service: onBind");
        return toMe.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        doToast("Stoping service!");
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //System.out.println("Service: onStartCommand");
        doToast("Service: onStartCommand");
        messageSimulator.schedule(new TimerTask() {
            @Override
            public void run() {
                String shownText = "Message received!! on " + (new Date(System.currentTimeMillis())).toString();
                qpidMessageReceived(shownText);
            }
        }, 5000, 5000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        doToast("Destroy service!");
        messageSimulator.cancel();
        messageSimulator = null;

        msgHandler = null;
        super.onDestroy();
    }

    protected void qpidMessageReceived(String msg)
    {
        if(toClient == null) return; //Who should I notify?
        Bundle msgPack = new Bundle();
        msgPack.putString("toToast", msg);
        Message m =  Message.obtain();
        m.setData(msgPack);
        try {
            toClient.send(m);
        } catch (RemoteException e) {
            e.printStackTrace();
            doToast("Could not send message to client!");
        }
    }


    //
    protected Messenger toClient;
    protected Messenger toMe;
    protected FromClientHandler msgHandler;

    protected class FromClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            toClient = msg.replyTo;
            //I wont answer from the moment, just keep the target (toClient) to sent messages to display
            //Bundle answer = new Bundle(); //Key-Map object used to pass objects.
            //answer.putString("description", ); //key, val
        }
    }


    //

    private Timer messageSimulator;
    private void doToast(String msg)
    {
        doToast(msg, this);
    }
    private void doToast(String msg, Context con) { Toast.makeText(con,msg,Toast.LENGTH_SHORT).show(); }
}
