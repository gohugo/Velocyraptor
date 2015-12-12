package ca.qc.bdeb.p55.velocyraptor.model;

import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Un point dans une course à un instant donné.
 */
public class RaceMarker implements Serializable {
    private final int secondsFromStart;
    transient private Location location;

    public RaceMarker(int seconds, Location p_location){
        secondsFromStart = seconds;
        location = p_location;
    }

    public RaceMarker(int seconds, double longitude, double latitude){
        secondsFromStart = seconds;
        location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }

    public int getSecondsFromStart(){
        return secondsFromStart;
    }

    public Location getLocation(){
        return location;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(location.getLatitude());
        out.writeDouble(location.getLongitude());
        out.writeUTF(location.getProvider());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        double latitude = in.readDouble();
        double longitude = in.readDouble();
        String provider = in.readUTF();

        location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }
}
