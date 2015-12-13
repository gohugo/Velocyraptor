package ca.qc.bdeb.p55.velocyraptor.model;

import ca.qc.bdeb.p55.velocyraptor.db.AppDatabase;

/**
 * Created by hugo on 2015-12-12.
 */
public class Achievement {
    public static final int BURN_ONE_CALORIE = 1;
    public static final int COMPLETE_FIRST_FOOT_RACE = 2;
    public static final int COMPLETE_FIRST_BIKE_RACE = 3;
    public static final int RUN_500_METERS = 4;
    public static final int RUN_1_KM = 5;
    public static final int RUN_2_KM = 6;
    public static final int RUN_5_KM = 7;
    public static final int RUN_MARATHON = 8;
    public static final int RUN_10_MINUTES = 9;
    public static final int RUN_20_MINUTES = 10;
    public static final int AVERAGE_SPEED_8KMH_OR_MORE = 11;
    public static final int AVERAGE_SPEED_11KMH_OR_MORE = 12;
    public static final int CYCLE_5_KM = 13;
    public static final int CYCLE_20_KM = 14;
    public static final int CYCLE_100_KM = 15;
    public static final int CYCLE_30_MINUTES = 16;
    public static final int CYCLE_1_HOUR = 17;
    public static final int CYCLE_90_MINUTES = 18;
    public static final int REACH_ALL_ACHIEVEMENTS = 19;

    public static final int[] ALL_ACHIEVEMENT_INDEXES = {
            BURN_ONE_CALORIE,
            COMPLETE_FIRST_FOOT_RACE,
            COMPLETE_FIRST_BIKE_RACE,
            RUN_500_METERS,
            RUN_1_KM,
            RUN_2_KM,
            RUN_5_KM,
            RUN_MARATHON,
            RUN_10_MINUTES,
            RUN_20_MINUTES,
            AVERAGE_SPEED_8KMH_OR_MORE,
            AVERAGE_SPEED_11KMH_OR_MORE,
            CYCLE_5_KM,
            CYCLE_20_KM,
            CYCLE_100_KM,
            CYCLE_30_MINUTES,
            CYCLE_1_HOUR,
            CYCLE_90_MINUTES,
            REACH_ALL_ACHIEVEMENTS
    };

    private int id;
    private boolean reached;

    public Achievement(int id, boolean reached){
        this.id = id;
        this.reached = reached;
    }

    public boolean isReached() {
        return reached;
    }

    public void markAsReached(){
        reached = true;
        AppDatabase.getInstance().markAchievementAsReached(id);
    }

    public int getId(){
        return id;
    }
}
