package ca.qc.bdeb.p55.velocyraptor.model;

import android.os.SystemClock;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Chronomètre simple.
 */
public class CustomChronometer implements Serializable {
    private static final int ON_TICK_DELAY = 50;

    private long startedAt;
    private int cumulativeTime = 0;
    private boolean onGoing = false;

    transient private Runnable onTick = null;
    transient private Timer tickTimer;

    public void start(){
        startedAt = SystemClock.elapsedRealtime();
        onGoing = true;
        tickTimer = new Timer();
        tickTimer.schedule(createRepeatedTimerTask(), ON_TICK_DELAY, ON_TICK_DELAY);
    }
    
    public void stop(){
        if(onGoing) {
            onGoing = false;
            cumulativeTime += SystemClock.elapsedRealtime() - startedAt;

            if (tickTimer != null)
                tickTimer.cancel();
        }
    }
    
    public int getElapsedMilliseconds(){
        if(onGoing)
            return cumulativeTime + (int) (SystemClock.elapsedRealtime() - startedAt);
        else
            return cumulativeTime;
    }

    /**
     * Définit la méthode qui sera appelée à chaque avancement du timer.
     * @param callback {@link Runnable} qui contient la méthode à éxécuter.
     */
    public void setOnTickCallback(Runnable callback){
        onTick = callback;

        if(onGoing && tickTimer == null){
            tickTimer = new Timer();
            tickTimer.schedule(createRepeatedTimerTask(), ON_TICK_DELAY, ON_TICK_DELAY);
        }
    }

    public void removeOnTickCallback(){
        if(tickTimer != null){
            tickTimer.cancel();
            tickTimer = null;
        }
    }

    /**
     * Crée un objet contnant une méthode appelée de façon répétée pour déclencher l'événement.
     */
    private TimerTask createRepeatedTimerTask(){
        return new TimerTask() {
            @Override
            public void run() {
                if(onTick != null)
                    onTick.run();
            }
        };
    }
}
