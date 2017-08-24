package in.reweyou.reweyouforums.civil;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import in.reweyou.reweyouforums.R;

public class DigitalLevel extends AppCompatActivity implements SensorEventListener {
    public static float f4x;
    public static float f5y;
    float f6z;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_level);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.mAccelerometer = this.mSensorManager.getDefaultSensor(1);

    }

    public final void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            f4x = event.values[0];
            f5y = -event.values[1];
            this.f6z = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();

        this.mSensorManager.registerListener(this, mAccelerometer, 3);
    }

    protected void onPause() {
        super.onPause();
        this.mSensorManager.unregisterListener(this);
    }

}
