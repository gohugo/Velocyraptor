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
        distance = 0;

        userPath = new ArrayList<>();
        chronometer = new CustomChronometer();
        stepCounter = new CustomStepCounter();
    }

    public void setContext(Context context) {
        stepCounter.setContext(context);
    }

    public TypeCourse getTypeCourse() {
        return typeCourse;
    }

    public State getState() {
        return state;
    }

    public void interrompre() {
        state = State.PAUSED;
        chronometer.stop();

        if (typeCourse == TypeCourse.APIED)
            stepCounter.stop();
    }

    public void demarrer() {
        state = State.STARTED;
        chronometer.start();

        if (typeCourse == TypeCourse.APIED)
            stepCounter.start();
    }

    public List<Location> getPath() {
        ArrayList<Location> locations = new ArrayList<>();

        for (RaceMarker marker : userPath)
            locations.add(marker.getLocation());

        return locations;
    }

    public void addLocation(Location location) {
        if (userPath.size() > 0) {
            Location lastLocation = userPath.get(userPath.size() - 1).getLocation();
            distance += lastLocation.distanceTo(location);
        }

        userPath.add(new RaceMarker(getElapsedSeconds(), location));
    }

    public int getElapsedMilliseconds() {
        return chronometer.getElapsedMilliseconds();
    }

    public int getElapsedSeconds(){
        return chronometer.getElapsedMilliseconds() / 1000;
    }

    public int getElapsedMinutes(){
        return chronometer.getElapsedMilliseconds() / 60000;
    }

    public int getDistanceInMeters() {
        return (int) Math.round(distance);
    }

    public int getCalories() {
        return (int) Math.round(distance * (typeCourse == TypeCourse.APIED
                ? CALORIES_PER_RUN_METER : CALORIES_PER_BIKE_METER));
    }

    public int getNbCountedSteps() {
        if (stepCounter.getNbCountedSteps() == -1)
            return (int) Math.round(distance / AVERAGE_STEP_LENGTH);

        return stepCounter.getNbCountedSteps();
    }

    public void setOnChronometerTick(Runnable callback) {
        chronometer.setOnTickCallback(callback);
    }

    public void removeOnChronometerTick() {
        chronometer.removeOnTickCallback();
    }

    /**
     * Enregistre cette course dans la BDD.
     * @return Tous les accomplissements qui ont été atteints durant cette course, s'il y en a.
     */
    public List<Achievement> endRaceAndSave() {
        chronometer.stop();
        state = State.STOPPED;
        AppDatabase.getInstance().addRace(userPath, this);
        return checkIfAchievementsAreReachedAndMarkThemAsReachedIfTheyAre();
    }

    private List<Achievement> checkIfAchievementsAreReachedAndMarkThemAsReachedIfTheyAre(){
        ArrayList<Achievement> reached = new ArrayList<>();
        List<Achievement> achievements = AppDatabase.getInstance().getAllAchievements();

        for(Achievement achievement : achievements){
            if(!achievement.isReached()){
                int id = achievement.getId();

                if(id == Achievement.BURN_ONE_CALORIE && getCalories() > 0)
                    reached.add(achievement);
                else if(id == Achievement.REACH_ALL_ACHIEVEMENTS && Achievement.areAllButLastAchievementsReached(achievements))
                    reached.add(achievement);
                else if(typeCourse == TypeCourse.APIED){
                    if(id == Achievement.COMPLETE_FIRST_FOOT_RACE)
                        reached.add(achievement);
                    else if(id == Achievement.RUN_500_METERS && getDistanceInMeters() >= 500)
                        reached.add(achievement);
                    else if(id == Achievement.RUN_1_KM && getDistanceInMeters() >= 1000)
                        reached.add(achievement);
                    else if(id == Achievement.RUN_2_KM && getDistanceInMeters() >= 2000)
                        reached.add(achievement);
                    else if(id == Achievement.RUN_5_KM && getDistanceInMeters() >= 5000)
                        reached.add(achievement);
                    else if(id == Achievement.RUN_MARATHON && getDistanceInMeters() >= 42200)
                        reached.add(achievement);
                    else if(id == Achievement.RUN_10_MINUTES && getElapsedMinutes() >= 10)
                        reached.add(achievement);
                    else if(id == Achievement.RUN_20_MINUTES && getElapsedMinutes() >= 20)
                        reached.add(achievement);
                    else if(id == Achievement.AVERAGE_SPEED_8KMH_OR_MORE && getAverageSpeed() >= 8)
                        reached.add(achievement);
                    else if(id == Achievement.AVERAGE_SPEED_11KMH_OR_MORE && getAverageSpeed() >= 11)
                        reached.add(achievement);
                } else {
                    if (id == Achievement.COMPLETE_FIRST_BIKE_RACE)
                        reached.add(achievement);
                    else if (id == Achievement.CYCLE_5_KM && getDistanceInMeters() >= 5000)
                        reached.add(achievement);
                    else if (id == Achievement.CYCLE_20_KM && getDistanceInMeters() >= 20000)
                        reached.add(achievement);
                    else if (id == Achievement.CYCLE_100_KM && getDistanceInMeters() >= 100000)
                        reached.add(achievement);
                    else if (id == Achievement.CYCLE_30_MINUTES && getElapsedMinutes() >= 30)
                        reached.add(achievement);
                    else if (id == Achievement.CYCLE_1_HOUR && getElapsedMinutes() >= 60)
                        reached.add(achievement);
                    else if (id == Achievement.CYCLE_90_MINUTES && getElapsedMinutes() >= 90)
                        reached.add(achievement);
                }
            }
        }

        for(Achievement reachedAchivement : reached){
            reachedAchivement.markAsReached();
        }

        return reached;
    }

    /**
     * Donne la vitesse moyenne en km/h.
     * @return Vitesse moyenne en km/h.
     */
    private double getAverageSpeed(){
        return (distance / getElapsedMilliseconds()) * 3600;
    }

    public enum TypeCourse {
        APIED, VELO
    }

    public enum State {
        STARTED, PAUSED, STOPPED
    }

}
