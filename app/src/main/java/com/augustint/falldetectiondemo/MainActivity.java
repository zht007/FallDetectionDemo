package com.augustint.falldetectiondemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity{

    private SensorManager manager;
    private SensorListener listener;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private double sVM;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    Sensor sensor =manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    //litsen accelerometer by register SensorListener
    listener = new SensorListener();
    manager.registerListener(listener, sensor, manager.SENSOR_DELAY_UI);//通过manager 获取速率

    }

    /**
     * this method is to vibrate
     */
    public void vibration()
    {
        /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern,2);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }

    /**
     * this method is to stop vibrate
     */
    public void onStop(){
        super.onStop();
        vibrator.cancel();
    }

//SensorListener method implements SensorEventListener
    private class SensorListener implements SensorEventListener {

    //Get the acceleration value
    @Override
    public void onSensorChanged(SensorEvent event) {

//        float x = event.values[0];
//        float y = event.values[1];
//        float z = event.values[2];

//            System.out.println("x: "+x);
//            System.out.println("y: "+y);
//            System.out.println("z: "+z);

        final float alpha =0.8f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        sVM = Math.sqrt(linear_acceleration[0] * linear_acceleration[0] + linear_acceleration[1] * linear_acceleration[1]+linear_acceleration[2] * linear_acceleration[2]);

        displayAcceleration(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2], sVM);


// set the SVM threshold

        if (sVM > 10){

        vibration();
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {



    }
}

    //反注册监听器
    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregisterListener(listener);
        listener = null;
    }

    /**
     * This methode is called when the onClickStart button is clicked
     */
    public void onClickStopVibration(View view)
    {
        //vibration();
        onStop();
        //onDestroy();
        // displayAcceleration(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);



    }
    /**
     * This methode is called when the onClickStop button is clicked
     */
    public void onClickStop(View view)
    {
        //vibration();
        //onStop();
        onDestroy();
       // displayAcceleration(linear_acceleration[0], linear_acceleration[1], linear_acceleration[2]);



    }

    /**
     * This method displays the Ax Ay and Az
     * @param pAx,pAy,pAz
     * @return
     */
    private void displayAcceleration(float pAx, float pAy, float pAz, double pSVM)
    {

           TextView TextViewAx = (TextView) findViewById(R.id.textViewAx);
           TextViewAx.setText("Ax = " + pAx);

           TextView TextViewAy = (TextView) findViewById(R.id.textViewAy);
           TextViewAy.setText("Ay = " + pAy);

           TextView TextViewAz = (TextView) findViewById(R.id.textViewAz);
           TextViewAz.setText("Az = " + pAz);

           TextView TextViewSVM = (TextView) findViewById(R.id.textViewSVM);
           TextViewSVM.setText("SVM = " + pSVM);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
