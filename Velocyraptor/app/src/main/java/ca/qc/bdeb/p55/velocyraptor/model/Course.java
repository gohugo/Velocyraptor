package ca.qc.bdeb.p55.velocyraptor.model;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.qc.bdeb.p55.velocyraptor.db.AppDatabase;

/**
 * Contient les données d'une course actuelle.
 */
public class Course implements Serializable {
    public enum TypeCourse {
        APIED, VELO
    }

    private ArrayList<RaceMarker> userPath;

    private TypeCourse typeCourse;

    private boolean enCours;

    public Course(){}

    public Course(TypeCourse typeCourse) {
        this.typeCourse = typeCourse;
        enCours = true;
        userPath = new ArrayList<>();
    }

    public TypeCourse getTypeCourse() {
        return typeCourse;
    }

    public boolean enCours() {
        return enCours;
    }

    public void interrompre(){
        enCours = false;
    }

    public void redemarrer(){
        enCours = true;
    }

    public List<Location> getPath(){
        ArrayList<Location> locations = new ArrayList<>();

        for(RaceMarker marker : userPath)
            locations.add(marker.location);

        return locations;
    }

    public void addLocation(int secondsFromStart, Location location){
        userPath.add(new RaceMarker(secondsFromStart, location));
    }

    /**
     * Enregistre cette course dans la BDD.
     * @param duration Durée totale en secondes.
     * @param calories Calories dépensées.
     * @param steps Nombre de pas, ou n'importe quelle valeur si c'est une course à vélo.
     */
    public void endRaceAndSave(int duration, int calories, int steps){
        AppDatabase.getInstance().addRace(userPath, typeCourse, duration, calories, steps);
    }
}
