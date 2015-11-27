package ca.qc.bdeb.p55.velocyraptor.model;

/**
 * Created by hugo on 2015-11-27.
 */
public class Chronometre extends Thread {
    public static long temps = 0;

    public void run() {


    }

    /*
    Permet de reset le temp du Timmer à 0:OO
     */
    public void reset() {

        this.setTemps(0);
    }

    public static long getTemps() {
        return temps;
    }

    public static void setTemps(long temps) {
        Chronometre.temps = temps;
    }
}
