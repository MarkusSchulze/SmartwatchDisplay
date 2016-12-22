package mi.hci.luh.de.smartwatchdisplay;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.sql.Date;
import java.util.List;
import java.sql.Timestamp;

import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Sensor accSensor, gyroSensor, accCleanSensor, gameRotationSensor, linearAccSensor;
    private SensorManager sensorManager;
    private Canvas canvas;
    private Paint paint;
    private Bitmap bg;
    private Button BigB;
    private int clickCount = 0;
    private float currentX, currentY, topY, bottomY, leftX, rightX, middleX, middleY;
    private float accX, accY;
    private double distX, distY, distX_right, distX_left, distY_top, distY_bottom;
    private boolean calibrated;
    private Timestamp lastLinAccTime;
    private boolean cursorEnabled;
    private CursorView cursorView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        LinearLayout rect  = (LinearLayout) findViewById(R.id.rect);
        cursorView = new CursorView(this);
        rect.addView(cursorView);

        //setContentView(cursorView);
//
//        paint = new Paint();
//        paint.setColor(Color.parseColor("#CD5C5C"));
//        bg = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
//        canvas = new Canvas(bg);
//        LinearLayout ll = (LinearLayout) findViewById(R.id.rect);
//        ll.setBackgroundDrawable(new BitmapDrawable(bg));


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            Log.d("MainActivity", sensor.toString());
        }

        accSensor           = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor          = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accCleanSensor      = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gameRotationSensor  = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        linearAccSensor     = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorManager.registerListener(this, accSensor, 100 * 1000);
        sensorManager.registerListener(this, gyroSensor, 100 * 1000);
        sensorManager.registerListener(this, accCleanSensor, 100 * 1000);
        sensorManager.registerListener(this, gameRotationSensor, 100 * 1000);

        BigB = (Button) findViewById(R.id.BigButton);

        BigB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ClickButton();
            }
        });
    }

    public void ClickButton() {
        if(this.cursorEnabled != true) {
            this.cursorEnabled = true;
            this.middleX = currentX;
            this.middleY = currentY;
        }
        else {
            this.cursorEnabled = false;
        }
        switch (clickCount) {
            case 0:
                Log.d("Calibration", "Start Calibration");
                BigB.setText("Choose Top");
                break;
            case 1:
                this.topY = currentY;
                this.distY_top = this.distY;
                Log.d("Calibration", String.format("topY: %f", this.topY));
                Log.d("Calibration", String.format("topY dist: %f", this.distY));
                BigB.setText("Choose Bottom");


                break;
            case 2:
                this.bottomY = currentY;
                this.distY_bottom = this.distY;
                Log.d("Calibration", String.format("bottomY: %f", this.bottomY));
                Log.d("Calibration", String.format("bottomY dist: %f", this.distY));
                BigB.setText("Choose Right");


                break;
            case 3:
                this.rightX = currentX;
                this.distX_right = this.distX;
                Log.d("Calibration", String.format("rightX: %f", this.rightX));
                Log.d("Calibration", String.format("rightX dist: %f", this.distX));
                BigB.setText("Choose Left");


                break;
            case 4:
                this.leftX = currentX;
                this.distX_left = this.distX;
                Log.d("Calibration", String.format("leftX: %f", this.leftX));
                Log.d("Calibration", String.format("leftX dist: %f", this.distX));
                BigB.setText("Finish");

                break;
            case 5:
                this.calibrated = true;
                Log.d("Calibration", "Calibration completed");
                BigB.setText("Completed");
                break;
            default:
                break;

        }
        clickCount++;
    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == gyroSensor) {
            float[] v = event.values;
        }
        if (event.sensor == accSensor) {
            float[] vs = event.values;
        }
        if (event.sensor == linearAccSensor) {
            float[] v = event.values;
            //Log.d("linAcc", String.format("%.3f\t%.3f\t%.3f", v[0], v[1], v[2]));

            float accX_old = accX;
            float accY_old = accY;

            // Filter Acceleration
            double threshold = 0.05;
            accX = ((v[1] < threshold) && (!(v[1] < -threshold))) ? 0 : v[1];
            accY = ((v[2] < threshold) && (!(v[2] < -threshold))) ? 0 : v[2];
            accY = (-1) * accY;
            //Log.d("Acceleration", String.format("accX: %f", v[1]));
            //Log.d("Acceleration", String.format("accY: %f", v[2]));


            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            //Log.d("currentTime", String.format("CurrentTime: %d", currentTime.getTime()));

            if (lastLinAccTime == null) {
                lastLinAccTime = currentTime;
            }
            double deltaTime = (currentTime.getTime() - lastLinAccTime.getTime());

            double deltaTime_square = Math.pow(deltaTime, 2.0) / 100000;

            distX = 0.5 * deltaTime_square * accX + deltaTime_square * accX_old + distX;
            distY = 0.5 * deltaTime_square * accY + deltaTime_square * accY_old + distY;

            lastLinAccTime = currentTime;
            //Log.d("deltaTime", String.format("DeltaTime: %d", deltaTime));
            Log.d("distX", String.format("distX: %f", distX));
            Log.d("distY", String.format("distY: %f", distY));
        }
        if (event.sensor == gameRotationSensor) {
            float[] vx = event.values;

            currentX = (-1) * vx[2];
            currentY = (-1) * vx[1];
//            int w = bg.getWidth();
//            int h = bg.getHeight();
            int w = cursorView.getWidth();
            int h = cursorView.getHeight();

            int point_x, point_y = 0;

            if (this.calibrated) {
                float mid_x_rot = (rightX + leftX) / 2;
                float mid_y_rot = (topY + bottomY) / 2;

                double mid_x_acc = (this.distX_right + this.distX_left) / 2;
                double mid_y_acc = (this.distY_top + this.distY_bottom) / 2;

                float x_rot = (currentX - mid_x_rot) / ((rightX - leftX) / 2);
                float y_rot = (currentY - mid_y_rot) / ((topY - bottomY) / 2);

                double x_dist = (distX - mid_x_acc) / ((distX_right - distX_left) / 2);
                double y_dist = (distY - mid_y_acc) / ((distY_top - distY_bottom) / 2);

                double x = 0.5 * x_rot + 0.5 * x_dist;
                double y = 0.5 * y_rot + 0.5 * y_dist;

                point_x = (int) (x_rot * w / 2 + w / 2);
                point_y = (int) (y_rot * h / 2 + h / 2);
            } else {
                point_x = (int) (currentX * h / 2) + h / 2;
                point_y = (int) (currentY * w / 2) + w / 2;
            }


            if (point_x > h) {
                point_x = h;
            }
            if (point_x < 0) {
                point_x = 0;
            }
            if (point_y > w) {
                point_y = w;
            }
            if (point_y < 0) {
                point_y = 0;
            }
//            canvas.drawRect(point_y, point_x, point_y + 10, point_x + 10, paint)
            cursorView.setCursor(point_y, point_x);
            cursorView.invalidate();
//            LinearLayout ll = (LinearLayout) findViewById(R.id.rect);
//            ll.setBackgroundDrawable(new BitmapDrawable(bg));

        }
    }

//    public double[] calibrate(double x, double y, double z) {
//
//
//    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("MainActivity", "onAccuracyChanged: " + sensor + ", " + accuracy);
    }
}
