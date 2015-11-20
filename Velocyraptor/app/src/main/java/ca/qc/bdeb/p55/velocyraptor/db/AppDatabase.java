package ca.qc.bdeb.p55.velocyraptor.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
     * @param applicationContext Contexte d'application
     */
    public static void setApplicationContext(Context applicationContext){
        if(instance == null)
            instance = new AppDatabase(applicationContext);
    }

    public static AppDatabase getInstance(){
        return instance;
    }

    private AppDatabase(Context applicationContext){
        super(applicationContext, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table races (" +
                COL_ID + " integer primary key autoincrement," +
                "type integer not null," +
                "length integer not null," +
                "distance integer not null," +
                "calories integer not null," +
                "steps integer" +
                ")");
        db.execSQL("create table achievements (" +
                COL_ID + " integer primary key autoincrement," +
                "reached tinyint not null default 0," +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
