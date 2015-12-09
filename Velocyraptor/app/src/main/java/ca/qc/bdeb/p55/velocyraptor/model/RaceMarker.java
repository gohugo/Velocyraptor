package ca.qc.bdeb.p55.velocyraptor.model;

import android.location.Location;

/**
 * Un point dans une course à un instant donné.
 */
public class RaceMarker {
    public final int secondsFromStart;
    public final Location location;

    public RaceMarker(int seconds, Location p_location){
        secondsFromStart = seconds;
        location = p_location;
    }
}
