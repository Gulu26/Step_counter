package com.example.step__counter

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlin.math.sqrt





class MainActivity : AppCompatActivity(), SensorEventListener{

    private var magnitutdePreviousStep = 0.0
    private var previousTotalSteps = 0f
    private var totalSteps= 0f
    private var running: Boolean = false
    private var sensorManger: SensorManager? = null
    private val ACTIVITY_RECOGNITION_REQUEST_CODE: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(isPermissionGranted()){
            requstPermission()

        }

        //load the step and reset the step to zero once it reched 3000
        loadData()
        resetStep()
      sensorManger = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private fun resetStep() {
        var steptaken = findViewById<TextView>(R.id.step_counter)

        steptaken.setOnClickListener {
            Toast.makeText( this, "long tap to reset", Toast.LENGTH_LONG).show()
        }
        steptaken.setOnLongClickListener {
            previousTotalSteps = totalSteps
            steptaken.text = 0.toString()
            saveData()
            true

        }
    }

    private fun saveData() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("step",
            Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("currentstep", previousTotalSteps)
        editor.apply()


    }

    private fun loadData() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("step",
            Context.MODE_PRIVATE)
        val savedNo: Float = sharedPreferences.getFloat("currentstep",0f )
        var perviousTotalSteps = savedNo

    }

    override fun onSensorChanged(event: SensorEvent?) {
        // let's write the major code
         var steptaken = findViewById<TextView>(R.id.step_counter)
         var cirbar = findViewById<CircularProgressBar>(R.id.circularProgressBar)
         //check whether the phone is moving or
         //this will not work for accelearometer sensor example example samsung or moto phones doesn't
         //soo wee add that acclearometer code
         if(event!!.sensor.type== Sensor.TYPE_ACCELEROMETER){
             //we need to detect the magnitude
             val xaccel: Float = event.values[0]
             val yaccel:Float = event.values[1]
             val zaccel:Float = event.values[2]
             val magnitutde: Double = sqrt((xaccel * xaccel + yaccel *yaccel + zaccel * zaccel).toDouble())

             var magnitudeDelta: Double = magnitutde - magnitutdePreviousStep
             magnitutdePreviousStep = magnitutde

             if(magnitudeDelta > 6){
                 totalSteps++


             }
             val step: Int = totalSteps.toInt()
             steptaken.text = step.toString()

             cirbar.apply {
                 setProgressWithAnimation(step.toFloat())
             }


         }else{
             if (running){
             totalSteps = event!!.values[0]
             val currentStep = totalSteps.toInt() - previousTotalSteps.toInt()
             steptaken.text= currentStep.toString()

             cirbar.apply {
                 setProgressWithAnimation(currentStep.toFloat())
             }
                 //now run the application


         }

             
         }
     }

     override fun onAccuracyChanged(p0:  Sensor?,  p1:Int) {

     }

     override fun onResume() {
         super.onResume()
         running = false

         val sensorManger = getSystemService(Context.SENSOR_SERVICE) as SensorManager
         //MOST rrecent moblie and major android mobile this sensor

         val countSensor = sensorManger?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
         val dectectorSensor = sensorManger?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
         //below sensor for particular sumsung and moto
         val accelerometer = sensorManger?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
         //now check which one is in your moblie
         when{
             countSensor != null ->{
                 sensorManger.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI)
             }
             dectectorSensor != null ->{
                 sensorManger.registerListener(this, dectectorSensor, SensorManager.SENSOR_DELAY_UI)

             }
             accelerometer != null ->{
                 sensorManger.registerListener(this, accelerometer , SensorManager.SENSOR_DELAY_UI)

             }
             else -> {
                 Toast.makeText(this, "Your device is not compatible", Toast.LENGTH_LONG)
             }
         }
     }

     override fun onPause() {
         super.onPause()
         sensorManger?. unregisterListener(this)
     }

     private fun requstPermission(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
            ACTIVITY_RECOGNITION_REQUEST_CODE)
        }

    }
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.ACTIVITY_RECOGNITION)!=PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            ACTIVITY_RECOGNITION_REQUEST_CODE ->{
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){

                }
            }
        }
    }
 }