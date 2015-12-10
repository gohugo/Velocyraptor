package ca.qc.bdeb.p55.velocyraptor.model;

import android.location.Location;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.qc.bdeb.p55.velocyraptor.db.AppDatabase;

/**
 * Contient les données d'une course actuelle.
 */
public class Course implements Serializable {
    private TypeCourse typeCourse;
    private ArrayList<RaceMarker> userPath;
    private CustomChronometer chronometer;

    private State state;

    public Course(TypeCourse typeCourse) {
        this.typeCourse = typeCourse;
        state = State.STARTED;
        userPath = new ArrayList<>();
        chronometer = new CustomChronometer();
        chronometer.start();
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
    }

    public void redemarrer(){
        state = State.STARTED;
        chronometer.start();
    }

    public List<Location> getPath(){
        ArrayList<Location> locations = new ArrayList<>();

        for(RaceMarker marker : userPath)
            locations.add(marker.location);

        return locations;
    }

    public void addLocation(Location location){
        userPath.add(new RaceMarker(chronometer.getElapsedSeconds(), location));
    }

    public String getFormattedElapsedTime(){
        return chronometer.toString();
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
        APIED, VELO
    }

    public enum State {
        STARTED, PAUSED, STOPPED
    }
}
