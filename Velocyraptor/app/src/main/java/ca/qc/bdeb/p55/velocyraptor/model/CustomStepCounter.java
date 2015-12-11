package ca.qc.bdeb.p55.velocyraptor.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.Serializable;

/**
 * Compte les pas parcourus par l'utilisateur.
 */
public class CustomStepCounter implements Serializable, SensorEventListener {
    transient private Context context;
    transient private SensorManager sensorManager;
    transient private Sensor stepDetector;

    /** Nombre de pas, ou -1 si le compteur de pas n'est pas disponible. */
    private int nbCountedSteps = 0;

    public void setContext(Context context){
        this.context = context;
    }

    public void start(){
        if(context == null)
            throw new IllegalStateException("Le contexte n'est pas défini.");

        if(sensorManager == null){
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }

        if(stepDetector != null)
            sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI);
        else
            nbCountedSteps = -1;
    }

    public void stop(){
        sensorManager.unregisterListener(this);
    }

    /**
     * @return Nombre exact de pas comptés, ou -1 si le service n'est pas disponible.
     */
    public int getNbCountedSteps(){
        return nbCountedSteps;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        nbCountedSteps++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

