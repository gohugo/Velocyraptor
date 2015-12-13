package ca.qc.bdeb.p55.velocyraptor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.qc.bdeb.p55.velocyraptor.db.AppDatabase;

/**
 * «Fantôme» d'une ancienne course, qui retourne les points passés.
 */
public class Ghost implements Serializable {
    public static Ghost startGhostFromLastRace(Course.TypeCourse typeCourse){
        Ghost ghost = new Ghost(AppDatabase.getInstance().getMarkersOf(typeCourse));

        return ghost;
    }

    /** Marqueurs non récupérés. */
    private final List<RaceMarker> unreadRaceMarkers;
    /** Marqueurs récupérés. */
    private final List<RaceMarker> readRaceMarkers = new ArrayList<>();

    private Ghost(List<RaceMarker> allMarkers){
        unreadRaceMarkers = allMarkers;
    }

    /**
     * Donne tous les marqueurs non récupérés avant le temps donné.
     * @param secondsFromStart Temps depuis le début de la course.
     * @return Tous les marqueurs non récupérés avant le temps donné en paramètre.
     */
    public List<RaceMarker> getNextMarkersAt(int secondsFromStart){
        ArrayList<RaceMarker> out = new ArrayList<>();

        while(unreadRaceMarkers.size() > 0
                && unreadRaceMarkers.get(0).getSecondsFromStart() <= secondsFromStart){
            out.add(unreadRaceMarkers.remove(0));
        }

        readRaceMarkers.addAll(out);

        return out;
    }

    public List<RaceMarker> getReadMarkers(){
        return Collections.unmodifiableList(readRaceMarkers);
    }
}
