package com.orionsword.qpidroid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

public class ServerConnection extends ActionBarActivity {

    Intent intent;

    public ServerConnection() {
        intent = new Intent();
        intent.setAction("com.orionsword.qpidroid.SUBSCRIBE");

        msgHandler = new ServerConnectionAppMsgHandler(this);
        fromService = new Messenger(msgHandler);
        ser_con = new QPIDServiceCon();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connection);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //CALLBACKS:
    public void onStartClick(View view)
    {
        //Starts the service
        startService(intent);
        //Bind to it
        isBound = bindService(intent, ser_con, Context.BIND_AUTO_CREATE);
        System.out.println("isBound" + Boolean.toString(isBound));
        if(!isBound)
        {
            Toast.makeText(this, "Problem found trying to bind to service!", Toast.LENGTH_SHORT);
            return;
        }
    }

    public void onStopClick(View view)
    {
        System.out.println("onStopClick!");
        unbindService(ser_con);
        isBound = false;
        stopService(intent);
    }
    //CALLBACKS END

    public void messageReceived(Bundle msgInfo)
    {
        String textToToast = msgInfo.getString("toToast");
        if(textToToast != null)
        {
            Toast.makeText(this, textToToast, Toast.LENGTH_SHORT).show();
            System.out.println("Client received: " + textToToast);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_server_connection, container, false);
            return rootView;
        }


    }

    protected class QPIDServiceCon implements ServiceConnection {

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            toService = new Messenger(iBinder);
            if(!isBound) return;
            System.out.println("is null toService?" + Boolean.toString(toService == null));
            Bundle rqData = new Bundle();
            rqData.putString("dummyField", "noval");
            Message msg2service = Message.obtain();
            msg2service.replyTo = fromService; //"fromservice" is my message receiver
            msg2service.setData(rqData);
            try {
                System.out.println("Before sending the message\t\ttoService:\t" + toService.toString());
                toService.send(msg2service);
                System.out.println("Finished onServiceConnected!");
            } catch (RemoteException e) {
                e.printStackTrace();
                System.out.println("Could not send the message!");
                return;
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            toService = null;
        }

    }

    protected ServerConnectionAppMsgHandler msgHandler;
    protected Messenger fromService;
    protected Messenger toService;
    protected QPIDServiceCon ser_con;
    protected boolean isBound = false;
}
