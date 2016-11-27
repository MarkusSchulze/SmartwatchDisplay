package mi.hci.luh.de.smartwatchdisplay;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {

    private SensorManager mSensorManager;
    private BubbleView bubbleView;
    private LinearLayout layout;
    private Sensor accel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // -----------------------------------------------
        // Drawing with Android Motion Sensors
        // statisch


        // dynamisch mit accel Sensorvalues
        bubbleView = new BubbleView(this);
        setContentView(bubbleView);
        accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // don't do anything; we don't care
    }
    public void onSensorChanged(SensorEvent event) {
        bubbleView.move(-event.values[0], event.values[1]);
        bubbleView.invalidate();
    }

    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        //mRenderer.start();
        //mGLSurfaceView.onResume();
        mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        //mRenderer.stop();
        //mGLSurfaceView.onPause();
        mSensorManager.unregisterListener(this);
    }

}
