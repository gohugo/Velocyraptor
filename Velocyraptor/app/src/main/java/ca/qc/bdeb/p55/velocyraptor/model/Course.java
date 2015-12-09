package ca.qc.bdeb.p55.velocyraptor.model;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contient les données d'une course actuelle.
 */
public class Course implements Serializable {
    public enum TypeCourse {
        APIED, VELO
    }

    private ArrayList<Location> userPath;

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
        return userPath;
    }

    public void addLocation(Location location){
        userPath.add(location);
    }
}
