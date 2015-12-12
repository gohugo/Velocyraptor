package ca.qc.bdeb.p55.velocyraptor.model;

/**
 * Created by hugo on 2015-12-11.
 */
public class HistoriqueDeCourse {
    private Course.TypeCourse typeCourse;
    private long time;
    private long date;
    private int totalDistance;
    private int nbStep;
    private int nbCalorieBurn;

    public HistoriqueDeCourse(Course.TypeCourse typeCourse, long time, long date, int nbStep, int totalDistance, int nbCalorieBurn) {
        this.typeCourse = typeCourse;
        this.time = time;
        this.date = date;
        this.nbStep = nbStep;
        this.totalDistance = totalDistance;
        this.nbCalorieBurn = nbCalorieBurn;
    }
    public HistoriqueDeCourse(Course.TypeCourse typeCourse,long time,  int totalDistance, int nbCalorieBurn,int nbStep) {
        this.typeCourse = typeCourse;
        this.time = time;
        this.nbStep = nbStep;
        this.totalDistance = totalDistance;
        this.nbCalorieBurn = nbCalorieBurn;


    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Course.TypeCourse getTypeCourse() {
        return typeCourse;
    }

    public void setTypeCourse(Course.TypeCourse typeCourse) {
        this.typeCourse = typeCourse;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getNbCalorieBurn() {
        return nbCalorieBurn;
    }

    public void setNbCalorieBurn(int nbCalorieBurn) {
        this.nbCalorieBurn = nbCalorieBurn;
    }

    public int getNbStep() {
        return nbStep;
    }

    public void setNbStep(int nbStep) {
        this.nbStep = nbStep;
    }
}