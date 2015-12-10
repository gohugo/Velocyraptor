package ca.qc.bdeb.p55.velocyraptor;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import java.text.DecimalFormat;

public class SuperChronometer extends TextView {
    @SuppressWarnings("unused")
    private static final String TAG = "Chronometer";
    private static final int TICK_WHAT = 2;

    private long lastTime;
    private long baseTime;
    /** Temps total écoulé en millisecondes. */
    private long timeElapsed;

    private boolean isVisible;
    private boolean isStarted;
    private boolean isRunning;

    public SuperChronometer(Context context) {
        this(context, null, 0);
    }

    public SuperChronometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperChronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        baseTime = SystemClock.elapsedRealtime();
        updateText(baseTime);
    }

    public void start() {
        baseTime = SystemClock.elapsedRealtime();
        isStarted = true;
        updateRunning();
    }


    public void stop() {
        isStarted = false;
        lastTime = 0;
        updateRunning();
    }
    public void  pause(){
        isStarted = false;
        lastTime = timeElapsed;
        updateRunning();
    }

    @Override
    protected void onDetachedFromWindow() {
        super .onDetachedFromWindow();
        isVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super .onWindowVisibilityChanged(visibility);
        isVisible = visibility == VISIBLE;
        updateRunning();
    }

    private synchronized void updateText(long now) {
        timeElapsed = now - baseTime + lastTime;

        DecimalFormat df = new DecimalFormat("00");

        int hours = (int)(timeElapsed / (3600 * 1000));
        int remaining = (int)(timeElapsed % (3600 * 1000));

        int minutes = remaining / (60 * 1000);
        remaining = remaining % (60 * 1000);

        int seconds = remaining / 1000;
        remaining = remaining % 1000;

        int hundrendths = remaining / 10;

        String text = "";

        if (hours > 0) {
            text += df.format(hours) + ":";
        }

        text += df.format(minutes) + ":";
        text += df.format(seconds) + ".";
        text += df.format(hundrendths);
        setText(text);
    }

    private void updateRunning() {
        boolean running = isVisible && isStarted;
        if (running != isRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
                mHandler.sendMessageDelayed(Message.obtain(mHandler,
                        TICK_WHAT), 100);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            isRunning = running;
        }
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (isRunning) {
                updateText(SystemClock.elapsedRealtime());
                sendMessageDelayed(Message.obtain(this , TICK_WHAT), 100);
            }
        }
    };

    public int getElapsedSeconds() {
        return (int) (timeElapsed / 1000);
    }

    @Override
    public Parcelable onSaveInstanceState(){
        return new SavedState(super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        savedState.restoreState();
    }

    public class SavedState extends BaseSavedState {
        private long lastTime;
        private long baseTime;
        private long timeElapsed;
        private boolean isVisible;
        private boolean isStarted;
        private boolean isRunning;

        public SavedState(Parcelable superState) {
            super(superState);

            lastTime = SuperChronometer.this.lastTime;
            baseTime = SuperChronometer.this.baseTime;
            timeElapsed = SuperChronometer.this.timeElapsed;
            isVisible = SuperChronometer.this.isVisible;
            isStarted = SuperChronometer.this.isStarted;
            isRunning = SuperChronometer.this.isRunning;
        }

        private SavedState(Parcel in) {
            super(in);
            lastTime = in.readLong();
            baseTime = in.readLong();
            timeElapsed = in.readLong();
            isVisible = (boolean) in.readValue(Boolean.class.getClassLoader());
            isStarted = (boolean) in.readValue(Boolean.class.getClassLoader());
            isRunning = (boolean) in.readValue(Boolean.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            Log.e("SAVEDSTATE", "writeToParcel");
            super.writeToParcel(out, flags);
            out.writeLong(lastTime);
            out.writeLong(baseTime);
            out.writeLong(timeElapsed);
            out.writeValue(isVisible);
            out.writeValue(isStarted);
            out.writeValue(isRunning);
        }

        private void restoreState(){
            SuperChronometer.this.lastTime = lastTime;
            SuperChronometer.this.baseTime = baseTime;
            SuperChronometer.this.timeElapsed = timeElapsed;
            SuperChronometer.this.isVisible = isVisible;
            SuperChronometer.this.isStarted = isStarted;
            SuperChronometer.this.isRunning = isRunning;
        }
    }

    public final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
        public SavedState createFromParcel(Parcel in) {
            Log.e("CREATOR", "createFromParcel");
            return new SavedState(in);
        }
        public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    };
}

