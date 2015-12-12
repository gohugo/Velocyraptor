package ca.qc.bdeb.p55.velocyraptor.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.qc.bdeb.p55.velocyraptor.model.Course;
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
                TABLE_RACES_LENGTH + " integer not null," +
                TABLE_RACES_DISTANCE + " integer not null," +
                TABLE_RACES_CALORIES + " integer not null," +
                TABLE_RACES_STEPS + " integer" +
                ")");
        db.execSQL("create table " + TABLE_ACHIEVEMENTS + " (" +
                COL_ID + " integer primary key autoincrement," +
                TABLE_ACHIEVEMENTS_REACHED + " tinyint not null default 0" +
                ")");
        db.execSQL("create table " + TABLE_LASTFOOTRACE + " (" +
                TABLE_RACE_SECONDSFROMSTART + " real not null," +
                TABLE_RACE_LONGITUDE + " real not null," +
                TABLE_RACE_LATITUDE + " real not null" +
                ")");
        db.execSQL("create table " + TABLE_LASTBIKERACE + " (" +
                TABLE_RACE_SECONDSFROMSTART + " real not null," +
                TABLE_RACE_LONGITUDE + " real not null," +
                TABLE_RACE_LATITUDE + " real not null" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Ajoute une course terminée et définit son chemin comme celui de la dernière course effectuée.
     *
     * @param markers    Endroits où l'utilisateur est passé.
     * @param course     Course.
     */
    public void addRace(List<RaceMarker> markers, Course course) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_RACES_TYPE, course.getTypeCourse().ordinal());
        values.put(TABLE_RACES_LENGTH, course.getElapsedSeconds());
        values.put(TABLE_RACES_DISTANCE, course.getDistanceInMeters());
        values.put(TABLE_RACES_CALORIES, course.getCalories());
        if(course.getTypeCourse() == Course.TypeCourse.APIED)
            values.put(TABLE_RACES_STEPS, course.getNbCountedSteps());
        db.insert(TABLE_RACES, null, values);

        String tableContainingThisRace = (course.getTypeCourse() == Course.TypeCourse.APIED
                ? TABLE_LASTFOOTRACE : TABLE_LASTBIKERACE);
        db.delete(tableContainingThisRace, null, null);

        for(RaceMarker marker : markers){
            values = new ContentValues();
            values.put(TABLE_RACE_SECONDSFROMSTART, marker.getSecondsFromStart());
            values.put(TABLE_RACE_LONGITUDE, marker.getLocation().getLongitude());
            values.put(TABLE_RACE_LATITUDE, marker.getLocation().getLatitude());
            db.insert(tableContainingThisRace, null, values);
        }

        db.close();
    }
    public ArrayList<Course> getAllLastRaces(){
        ArrayList <Course>  lstCourses = new ArrayList<>();


        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuerry = "SELECT * FROM " + TABLE_RACES;
        Cursor cursor = db.rawQuery(selectQuerry, null);

        if (cursor != null) {
            cursor.moveToFirst();
            do {
                String abc = cursor.getString(0);

                lstCourses.add(new Course();
            } while (cursor.moveToNext());
        }

        return lstCourses;
    }
}
