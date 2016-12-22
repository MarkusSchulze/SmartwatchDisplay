package mi.hci.luh.de.smartwatchdisplay;

import android.app.Activity;
import android.app.PendingIntent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


public class WearableAPI extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener{

        GoogleApiClient mGoogleApiClient;


        public PlaceholderFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mGoogleApiClient.disconnect();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            rootView.findViewById(R.id.wearable).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mGoogleApiClient.isConnected()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                                for(Node node : nodes.getNodes()) {
                                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), SyncStateContract.Constants.PATH_NOTIFICAITON_MESSAGE, "Hello World".getBytes()).await();
                                    if(!result.getStatus().isSuccess()){
                                        Log.e("test", "error");
                                    } else {
                                        Log.i("test", "success!! sent to: " + node.getDisplayName());
                                    }
                                }
                            }
                        }).start();

                    } else {
                        Log.e("test", "not connected");
                    }

                }
            });
            rootView.findViewById(R.id.oldschool).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotificationManager man = (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);
                    man.notify(0, getNotification().build());
                }
            });
            return rootView;
        }

        NotificationCompat.Builder getNotification() {
            Intent viewIntent = new Intent(getActivity(), WearableAPI.class);
//            viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
            PendingIntent viewPendingIntent = PendingIntent.getActivity(getActivity(), 0, viewIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
            builder.setLargeIcon(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_launcher));
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setSubText("Its Bed Time");
            builder.setContentTitle("Wear Notification Test");
            builder.setContentText("Wear Notification Text");
//            builder.setContentIntent(viewPendingIntent);
            return builder;
        }

        @Override
        public void onConnected(Bundle bundle) {
            Log.d("test", "onConnected");
        }

        @Override
        public void onConnectionSuspended(int i) {
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e("test", "Failed to connect to Google API Client");
        }
    }


}