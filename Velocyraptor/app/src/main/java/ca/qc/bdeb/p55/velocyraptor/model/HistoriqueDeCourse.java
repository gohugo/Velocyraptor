package ca.qc.bdeb.p55.velocyraptor.model;

/**
 * Created by hugo on 2015-12-11.
 */
public class HistoriqueDeCourse {
    private Course.TypeCourse typeCourse;
    private int duration;
    private int totalDistance;
    private int nbStep;
    private int nbCalorieBurn;
    private long timestamp;

    public HistoriqueDeCourse(Course.TypeCourse typeCourse, int durationInMilliseconds,
                              int totalDistance, int nbCaloriesBurned, int nbSteps, long timestamp) {
        this.typeCourse = typeCourse;
        this.duration = durationInMilliseconds;
        this.nbStep = nbSteps;
        this.totalDistance = totalDistance;
        this.nbCalorieBurn = nbCaloriesBurned;
        this.timestamp = timestamp;
    }

    public Course.TypeCourse getTypeCourse() {
        return typeCourse;
    }

    public int getDurationInMilliseconds(){
        return duration;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public int getNbCalorieBurn() {
        return nbCalorieBurn;
    }

    public int getNbStep() {
        return nbStep;
    }

    public long getTimestamp(){
        return timestamp;
    }
}
