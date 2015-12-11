package ca.qc.bdeb.p55.velocyraptor.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    private final String TABLE_RACE = "races";
    private final String TABLE_RACE_TYPE = "type";
    private final String TABLE_RACE_LENGTH = "length";
    private final String TABLE_RACE_DISTANCE = "distance";
    private final String TABLE_RACE_CALORIES = "calories";
    private final String TABLE_RACE_STEPS = "steps";



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_RACE + " (" +
                COL_ID + " integer primary key autoincrement," +
                TABLE_RACE_TYPE + " integer not null," +
                TABLE_RACE_LENGTH + " integer not null," +
                TABLE_RACE_DISTANCE + " integer not null," +
                TABLE_RACE_CALORIES + " integer not null," +
                TABLE_RACE_STEPS + " integer" +
                ")");
        db.execSQL("create table achievements (" +
                COL_ID + " integer primary key autoincrement," +
                "reached tinyint not null default 0" +
                ")");
        db.execSQL("create table lastrace (" +
                "seconds real not null," +
                "longitude real not null," +
                "latitude real not null" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Ajoute une course terminée et définit son chemin comme celui de la dernière course effectuée.
     *
     * @param markers    Endroits où l'utilisateur est passé.
     * @param typeCourse Type de course.
     * @param duration   Durée, en secondes.
     * @param calories   Nombre de calories dépensées.
     * @param steps      Nombre de pas effectués. Cette valeur sera ignorée si c'est une course à vélo.
     */


    public void addRace(List<RaceMarker> markers, Course.TypeCourse typeCourse, int duration, int calories, int steps) {


        SQLiteDatabase db = this.getWritableDatabase(); // On veut écrire dans la BD
        ContentValues values = new ContentValues();
        System.out.println("ordinal Type de course = " +typeCourse.ordinal());
        values.put(TABLE_RACE_TYPE, typeCourse.ordinal()); // Nom du client
        values.put(TABLE_RACE_LENGTH, duration);
       //entrer la vrai distance
        values.put(TABLE_RACE_DISTANCE, 0);
        values.put(TABLE_RACE_CALORIES, calories);
        values.put(TABLE_RACE_STEPS, steps);
        long id = db.insert(TABLE_RACE, null, values);
        db.close(); // Fermer la connexion


    }
}
