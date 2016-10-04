package com.z_waligorski.przyrodnik;

// Observation objects are used for communication with database
public class Observation {

    private  int id;
    private String noteTitle;
    private String note;
    private String photo;
    private double longitude;
    private double latitude;
    private String date;

    public Observation(){}

    public Observation(String noteTitle, String note, String photo, double longitude, double latitude, String date) {
        this.noteTitle = noteTitle;
        this.note = note;
        this.photo = photo;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }

    public Observation(int id, String noteTitle, String note, String photo, double longitude, double latitude, String date) {
        this.id = id;
        this.noteTitle = noteTitle;
        this.note = note;
        this.photo = photo;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}