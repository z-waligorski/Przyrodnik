package com.z_waligorski.przyrodnik;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddObservationActivity extends AppCompatActivity implements LocationProvider.LocationCallback {

    private EditText note;
    private EditText noteTitle;
    private TextView coordinates;
    private ImageView photoImageView;

    double longitude;
    double latitude;
    private String noteTitleText;
    private String noteText;

    private Uri fileUri;
    private String uriText;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Location location;
    private LocationProvider locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_observation);

        note = (EditText) findViewById(R.id.note);
        noteTitle = (EditText) findViewById(R.id.note_title);
        coordinates = (TextView) findViewById(R.id.coordinates);
        photoImageView = (ImageView) findViewById(R.id.photo_image_view);

        locationProvider = new LocationProvider(this, this);

        // Load photo to ImageView after screen rotation
        if(savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable("imageUri");
            photoImageView.setImageBitmap(getBitmapByUri(fileUri));
        }
    }

    // Keep image uri during screen rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("imageUri", fileUri);
        super.onSaveInstanceState(outState);
    }

    // Add menu to Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return true;
    }

    // Menu icon launches web browser
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_browser_available, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    // Save observation. If location is not available ask user what to do
    public void saveClicked(View view) {
        noteTitleText = noteTitle.getText().toString();
        noteText = note.getText().toString();
        if(fileUri == null) {
            uriText = "";
        } else {
            uriText = fileUri.toString();
        }
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            saveObservation(this);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_location)
                    .setMessage(R.string.save_or_wait)
                    .setPositiveButton(R.string.no_location_save_without, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            longitude = 181;
                            latitude = 91;

                            saveObservation(getApplicationContext());
                        }
                    })
                    .setNegativeButton(R.string.wait_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    public void saveObservation(Context context) {
        String date = new SimpleDateFormat("d.MM.yy").format(new Date());

        Observation observation = new Observation(noteTitleText, noteText, uriText, longitude, latitude, date);
        ObservationDatabase observationDB = new ObservationDatabase(context);
        observationDB.addObservation(observation);

        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }

    // Launch camera. If photo was already taken inform user that it will be deleted
    public void launchCamera(View view) {
        if(fileUri == null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = getOutputFileUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.alert)
                    .setMessage(R.string.picture_will_be_deleted)
                    .setPositiveButton(R.string.continue_string, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new File(fileUri.getPath()).delete();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            fileUri = getOutputFileUri();
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        }
                    })
                    .setNegativeButton(R.string.drop, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    // After taking photo load it to image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoImageView.setImageBitmap(getBitmapByUri(fileUri));
        }
    }

    public Bitmap getBitmapByUri (Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // Create file to store photo
    private File getOutputFile() {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Przyrodnik");
        if(!storageDir.exists())
            storageDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File outputFile = new File(storageDir.getPath() + File.separator + "IMG_" + timeStamp + ".JPG");
        return outputFile;
    }

    // Get uri of file that was created
    private Uri getOutputFileUri() {
        return Uri.fromFile(getOutputFile());
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationProvider.disconnect();
    }

    // Executed when LocationProvider provides new location
    public void newLocation(Location location) {
        this.location = location;
        coordinates.setText(String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
    }

    // Delete image file if user didn't save observation and pressed back button
    @Override
    public void onBackPressed() {
        if(fileUri != null) {
            new File(fileUri.getPath()).delete();
        }
        super.onBackPressed();
    }
}
