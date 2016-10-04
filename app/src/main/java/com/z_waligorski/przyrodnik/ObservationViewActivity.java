package com.z_waligorski.przyrodnik;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ObservationViewActivity extends AppCompatActivity {

    private EditText observationTitle;
    private EditText observationText;
    private TextView observationDate;
    private ImageView observationImage;

    private String id;
    private double longitude;
    private double latitude;
    private String title;

    private Uri uri;

    private ObservationDatabase observationDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_view);

        observationTitle = (EditText) findViewById(R.id.observation_title);
        observationText = (EditText) findViewById(R.id.observation_text);
        observationDate = (TextView) findViewById(R.id.observation_date);
        observationImage = (ImageView) findViewById(R.id.observation_image);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        // Get data from database
        observationDatabase = new ObservationDatabase(this);
        Observation observation = observationDatabase.getObservation(id);

        title = observation.getNoteTitle().toString();
        longitude = observation.getLongitude();
        latitude = observation.getLatitude();

        observationTitle.setText(title);
        observationText.setText(observation.getNote().toString());
        observationDate.setText(observation.getDate().toString());

        // Get image from folder and load it to ImageView
        Bitmap photo = null;
        uri = Uri.parse(observation.getPhoto());
        try {
            photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        observationImage.setImageBitmap(photo);

        // When activity launches keyboard should be hidden
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // Toolbar displays save and delete icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view, menu);
        return true;
    }

    // Save and delete actions added for Toolbar icons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.save) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("title", observationTitle.getText().toString());
            map.put("note", observationText.getText().toString());
            map.put("id", id);

            observationDatabase.updateObservation(map);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return true;
        } else {
            observationDatabase.deleteObservation(id);
            new File(uri.getPath()).delete();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return true;
        }
    }

    // onClick method - if observation contains valid location show it on map in ObservationMapActivity
    public void showOnMap(View view) {
        if (longitude != 181) {
            Intent intent = new Intent(this, ObservationMapActivity.class);
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            intent.putExtra("title", title);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_location, Toast.LENGTH_SHORT).show();
        }
    }
}
