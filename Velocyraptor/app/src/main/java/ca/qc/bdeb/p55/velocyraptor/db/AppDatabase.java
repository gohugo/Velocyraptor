package ca.qc.bdeb.p55.velocyraptor.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import ca.qc.bdeb.p55.velocyraptor.model.Achievement;
import ca.qc.bdeb.p55.velocyraptor.model.Course;
import ca.qc.bdeb.p55.velocyraptor.model.ItemCourse;
import ca.qc.bdeb.p55.velocyraptor.model.RaceMarker;

/**
 * Base de données de l'application.
 */
public class AppDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "app.db";
    private static final int DB_VERSION = 1;

    private static final String COL_ID = "_id";

    private static final String TABLE_RACES = "races";
    private static final String TABLE_RACES_TYPE = "type";
    private static final String TABLE_RACES_TIMESTAMP = "timestamp";
    private static final String TABLE_RACES_LENGTH = "length";
    private static final String TABLE_RACES_DISTANCE = "distance";
    private static final String TABLE_RACES_CALORIES = "calories";
    private static final String TABLE_RACES_STEPS = "steps";

    private static final String TABLE_ACHIEVEMENTS = "achievements";
    private static final String TABLE_ACHIEVEMENTS_REACHED = "reached";

    private static final String TABLE_LASTFOOTRACE = "lastfootrace";
    private static final String TABLE_LASTBIKERACE = "lastbikerace";
    private static final String TABLE_RACE_SECONDSFROMSTART = "seconds";
    private static final String TABLE_RACE_LONGITUDE = "longitude";
    private static final String TABLE_RACE_LATITUDE = "latitude";

    private static AppDatabase instance;

    /**
     * Définit le contexte d'application pour la BDD. Doît être applée au minimum au
     * tout début de l'application.
     *
     * @param applicationContext Contexte d'application
     */
    public static void setApplicationContext(Context applicationContext) {
        if (instance == null)
            instance = new AppDatabase(applicationContext);
    }

    public static AppDatabase getInstance() {
        return instance;
    }

    private AppDatabase(Context applicationContext) {
        super(applicationContext, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_RACES + " (" +
                COL_ID + " integer primary key autoincrement," +
                TABLE_RACES_TYPE + " integer not null," +
                TABLE_RACES_TIMESTAMP + " integer not null," +
                TABLE_RACES_LENGTH + " integer not null," +
                TABLE_RACES_DISTANCE + " integer not null," +
                TABLE_RACES_CALORIES + " integer not null," +
                TABLE_RACES_STEPS + " integer" +
                ")");
        db.execSQL("create table " + TABLE_ACHIEVEMENTS + " (" +
                COL_ID + " integer primary key," +
                TABLE_ACHIEVEMENTS_REACHED + " tinyint not null default 0" +
                ")");

        for (String tableName : new String[]{TABLE_LASTFOOTRACE, TABLE_LASTBIKERACE}) {
            db.execSQL("create table " + tableName + " (" +
                    TABLE_RACE_SECONDSFROMSTART + " integer not null," +
                    TABLE_RACE_LONGITUDE + " real not null," +
                    TABLE_RACE_LATITUDE + " real not null" +
                    ")");
        }

        initializeAllAchievements(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Ajoute une course terminée et définit son chemin comme celui de la dernière course effectuée,
     * et sa date comme étant la date actuelle.
     *
     * @param markers Endroits où l'utilisateur est passé.
     * @param course  Course.
     */
    public void addRace(List<RaceMarker> markers, Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_RACES_TYPE, course.getTypeCourse().ordinal());
        values.put(TABLE_RACES_TIMESTAMP, new Date().getTime() / 1000);
        values.put(TABLE_RACES_LENGTH, course.getElapsedMilliseconds());
        values.put(TABLE_RACES_DISTANCE, course.getDistanceInMeters());
        values.put(TABLE_RACES_CALORIES, course.getCalories());
        if (course.getTypeCourse() == Course.TypeCourse.APIED)
            values.put(TABLE_RACES_STEPS, course.getNbCountedSteps());
        db.insert(TABLE_RACES, null, values);

        String tableContainingThisRace = getRaceTableFromType(course.getTypeCourse());
        db.delete(tableContainingThisRace, null, null);

        for (RaceMarker marker : markers) {
            values = new ContentValues();
            values.put(TABLE_RACE_SECONDSFROMSTART, marker.getSecondsFromStart());
            values.put(TABLE_RACE_LONGITUDE, marker.getLocation().getLongitude());
            values.put(TABLE_RACE_LATITUDE, marker.getLocation().getLatitude());
            db.insert(tableContainingThisRace, null, values);
        }

        db.close();
    }


    /**
     * Retourne tous les marqueurs de la dernière course d'un type.
     *
     * @param typeCourse Type de la dernière course.
     * @return Marqueurs de cette course.
     */
    public List<RaceMarker> getMarkersOf(Course.TypeCourse typeCourse) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RaceMarker> markers = new ArrayList<>();

        Cursor cursor = db.query(getRaceTableFromType(typeCourse), null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int secondsFromStart = cursor.getInt(cursor.getColumnIndex(TABLE_RACE_SECONDSFROMSTART));
                double longitude = cursor.getDouble(cursor.getColumnIndex(TABLE_RACE_LONGITUDE));
                double latitude = cursor.getDouble(cursor.getColumnIndex(TABLE_RACE_LATITUDE));
                markers.add(new RaceMarker(secondsFromStart, longitude, latitude));
            } while (cursor.moveToNext());
        }

        db.close();

        return markers;
    }

    public int getNumberRaces(Course.TypeCourse typeCourse) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from " + TABLE_RACES
                + " where " + TABLE_RACES_TYPE + " = ?", new String[]{String.valueOf(typeCourse.ordinal())});

        cursor.moveToFirst();
        int result = cursor.getInt(0);
        db.close();

        return result;
    }

    public int getTotalDurationInSeconds(Course.TypeCourse typeCourse) {
        return (int) (getSumOfRaceColumn(TABLE_RACES_LENGTH, typeCourse) / 1000);
    }

    public int getTotalDistance(Course.TypeCourse typeCourse) {
        return (int) getSumOfRaceColumn(TABLE_RACES_DISTANCE, typeCourse);
    }

    public int getTotalCalories(Course.TypeCourse typeCourse) {
        return (int) getSumOfRaceColumn(TABLE_RACES_CALORIES, typeCourse);
    }

    public int getTotalSteps() {
        return (int) getSumOfRaceColumn(TABLE_RACES_STEPS, Course.TypeCourse.APIED);
    }

    private long getSumOfRaceColumn(String column, Course.TypeCourse typeCourse) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select sum(" + column + ") from " + TABLE_RACES
                + " where " + TABLE_RACES_TYPE + " = ?", new String[]{String.valueOf(typeCourse.ordinal())});
        cursor.moveToFirst();
        long result = cursor.getLong(0);
        db.close();
        return result;
    }


    public List<ItemCourse> getAllLastRaces() {
        ArrayList<ItemCourse> lstCourses = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuerry = "SELECT * FROM " + TABLE_RACES + " order by " + COL_ID + " DESC";
        Cursor cursor = db.rawQuery(selectQuerry, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Course.TypeCourse typeCourse = cursor.getInt(cursor.getColumnIndex(TABLE_RACES_TYPE))
                        == Course.TypeCourse.APIED.ordinal() ? Course.TypeCourse.APIED : Course.TypeCourse.VELO;
                int duration = cursor.getInt(cursor.getColumnIndex(TABLE_RACES_LENGTH));
                int distance = cursor.getInt(cursor.getColumnIndex(TABLE_RACES_DISTANCE));
                int calories = cursor.getInt(cursor.getColumnIndex(TABLE_RACES_CALORIES));
                int steps = cursor.getInt(cursor.getColumnIndex(TABLE_RACES_STEPS));
                int timestamp = cursor.getInt(cursor.getColumnIndex(TABLE_RACES_TIMESTAMP));

                lstCourses.add(new ItemCourse(typeCourse, duration, distance, calories, steps, timestamp));
            } while (cursor.moveToNext());
        }

        return lstCourses;
    }

    public List<Achievement> getAllAchievements() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Achievement> achievements = new ArrayList<>();

        final String selectQuery = "SELECT * FROM " + TABLE_ACHIEVEMENTS
                + " order by " + TABLE_ACHIEVEMENTS_REACHED + " desc";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
                boolean isReached = cursor.getInt(cursor.getColumnIndex(TABLE_ACHIEVEMENTS_REACHED)) == 1;
                achievements.add(new Achievement(id, isReached));
            } while (cursor.moveToNext());
        }

        return achievements;
    }

    public void markAchievementAsReached(int id){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_ACHIEVEMENTS_REACHED, true);
        db.update(TABLE_ACHIEVEMENTS, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /*
méthode qui initialise les succes de base de l"application et qui les sauvegardes dans la bd
pour que l'on puisse sauvegader si il on été*/
    private void initializeAllAchievements(SQLiteDatabase db) {
        for(int achievement : Achievement.ALL_ACHIEVEMENT_INDEXES) {
            ContentValues values = new ContentValues();
            values.put(COL_ID, achievement);
            values.put(TABLE_ACHIEVEMENTS_REACHED, false);
            db.insert(TABLE_ACHIEVEMENTS, null, values);
        }
    }


    private String getRaceTableFromType(Course.TypeCourse typeCourse) {
        return typeCourse == Course.TypeCourse.APIED ? TABLE_LASTFOOTRACE : TABLE_LASTBIKERACE;
    }
}
