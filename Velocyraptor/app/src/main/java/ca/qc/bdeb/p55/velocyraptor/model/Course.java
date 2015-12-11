package ca.qc.bdeb.p55.velocyraptor.model;

import android.content.Context;
import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.qc.bdeb.p55.velocyraptor.db.AppDatabase;

/**
 * Contient les données d'une course actuelle.
 */
public class Course implements Serializable {
    private static final double CALORIES_PER_RUN_METER = 0.069904125;
    private static final double CALORIES_PER_BIKE_METER = 0.01926247;
    private static final double AVERAGE_STEP_LENGTH = 1.2;

    private TypeCourse typeCourse;
    private ArrayList<RaceMarker> userPath;
    private double distance;
    private CustomChronometer chronometer;
    private CustomStepCounter stepCounter;

    private State state;

    public Course(TypeCourse typeCourse) {
        this.typeCourse = typeCourse;
        state = State.STOPPED;
        userPath = new ArrayList<>();
        distance = 0;
        chronometer = new CustomChronometer();
        stepCounter = new CustomStepCounter();
    }

    public void setContext(Context context){
        stepCounter.setContext(context);
    }

    public TypeCourse getTypeCourse() {
        return typeCourse;
    }

    public State getState() {
        return state;
    }

    public void interrompre(){
        state = State.PAUSED;
        chronometer.stop();
        stepCounter.stop();
    }

    public void demarrer(){
        state = State.STARTED;
        chronometer.start();
        stepCounter.start();
    }

    public List<Location> getPath(){
        ArrayList<Location> locations = new ArrayList<>();

        for(RaceMarker marker : userPath)
            locations.add(marker.getLocation());

        return locations;
    }

    public void addLocation(Location location){
        if(userPath.size() > 0) {
            Location lastLocation = userPath.get(userPath.size() - 1).getLocation();
            distance += lastLocation.distanceTo(location);
        }
        userPath.add(new RaceMarker(chronometer.getElapsedSeconds(), location));
    }

    public String getFormattedElapsedTime(){
        return chronometer.toString();
    }

    public int getDistanceInMeters(){
        return (int) Math.round(distance);
    }

    public int getCalories(){
        return (int) Math.round(distance * (typeCourse == TypeCourse.PIED
                ? CALORIES_PER_RUN_METER : CALORIES_PER_BIKE_METER));
    }

    public int getNbCountedSteps(){
        if(stepCounter.getNbCountedSteps() == -1)
            return (int) Math.round(distance / AVERAGE_STEP_LENGTH);

        return stepCounter.getNbCountedSteps();
    }

    public void setOnChronometerTick(Runnable callback){
        chronometer.setOnTickCallback(callback);
    }

    public void removeOnChronometerTick(){
        chronometer.removeOnTickCallback();
    }

    /**
     * Enregistre cette course dans la BDD.
     * @param calories Calories dépensées.
     * @param steps Nombre de pas, ou n'importe quelle valeur si c'est une course à vélo.
     */
    public void endRaceAndSave(int calories, int steps){
        chronometer.stop();
        state = State.STOPPED;
        AppDatabase.getInstance().addRace(userPath, typeCourse, chronometer.getElapsedSeconds(), calories, steps);
    }

    public enum TypeCourse {
        PIED, VELO
    }

    public enum State {
        STARTED, PAUSED, STOPPED
    }
}
