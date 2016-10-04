package com.z_waligorski.przyrodnik;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class ObservationDatabase extends SQLiteOpenHelper {

    // Database name
    private static final String DATABASE_NAME = "ObservationDatabase";

    // Table name
    private static final String TABLE_OBSERVATION = "ObservationTable";

    // Columns names
    private static final String ID = "id";
    private static final String NOTE_TITLE = "noteTitle";
    private static final String NOTE = "note";
    private static final String PHOTO = "photo";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String DATE = "date";

    public ObservationDatabase(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    // Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_OBSERVATION
                + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NOTE_TITLE + " TEXT,"
                + NOTE + " TEXT,"
                + PHOTO + " TEXT,"
                + LONGITUDE + " REAL,"
                + LATITUDE + " REAL,"
                + DATE + " TEXT"
                + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_OBSERVATION);
        onCreate(db);
    }

    // Add new observation
    public void addObservation(Observation observation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTE_TITLE, observation.getNoteTitle());
        values.put(NOTE, observation.getNote());
        values.put(PHOTO, observation.getPhoto());
        values.put(LONGITUDE, observation.getLongitude());
        values.put(LATITUDE, observation.getLatitude());
        values.put(DATE, observation.getDate());

        db.insert(TABLE_OBSERVATION, null, values);
        db.close();
    }

    // Update title and note of already existing observation
    public void updateObservation(HashMap<String,String> updateMap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTE_TITLE, updateMap.get("title"));
        values.put(NOTE, updateMap.get("note"));

        db.update(TABLE_OBSERVATION, values, "id = ?", new String[] {updateMap.get("id")});
        db.close();
    }

    public void deleteObservation(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_OBSERVATION + " WHERE id = '"+ id +"'";
        db.execSQL(deleteQuery);
        db.close();
    }

    // Get all observations
    public ArrayList<Observation> getAllObservations() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Observation> observationsList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_OBSERVATION;

        Cursor cursor = db.rawQuery(query, null);
         if(cursor.moveToFirst()) {
             do {
                 Observation observation = new Observation();
                 observation.setId(cursor.getInt(0));
                 observation.setNoteTitle(cursor.getString(1));
                 observation.setNote(cursor.getString(2));
                 observation.setPhoto(cursor.getString(3));
                 observation.setLongitude(cursor.getDouble(4));
                 observation.setLatitude(cursor.getDouble(5));
                 observation.setDate(cursor.getString(6));

                 observationsList.add(observation);
             } while (cursor.moveToNext());
         }
        return observationsList;
    }

    // Get single observation
    public Observation getObservation(String id) {
        Observation observation = new Observation();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_OBSERVATION + " WHERE id = '" + id + "'";
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {

            do {
                int noteId = cursor.getInt(0);
                String noteTitle = cursor.getString(1);
                String note = cursor.getString(2);
                String photo = cursor.getString(3);
                double longitude = cursor.getDouble(4);
                double latitude = cursor.getDouble(5);
                String date = cursor.getString(6);

                observation.setId(noteId);
                observation.setNoteTitle(noteTitle);
                observation.setNote(note);
                observation.setPhoto(photo);
                observation.setLongitude(longitude);
                observation.setLatitude(latitude);
                observation.setDate(date);
            } while (cursor.moveToNext());
        }
        return observation;
    }
}
