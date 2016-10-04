package com.z_waligorski.przyrodnik;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Provides data from database
        ObservationDatabase observationDB = new ObservationDatabase(this);
        ArrayList<Observation> observationsList = new ArrayList<>();
        observationsList = observationDB.getAllObservations();

        // Setup RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Create adapter for RecyclerView with onClickListener
        adapter = new RecyclerAdapter(observationsList);
        adapter.setOnItemClickListener(new RecyclerAdapter.ClickListener() {
            @Override
            public void onItemClicked(int position, View view) {
                TextView idTextView = (TextView) view.findViewById(R.id.recycler_note_id);
                String noteId = idTextView.getText().toString();
                Intent intent = new Intent(getApplicationContext(), ObservationViewActivity.class);
                intent.putExtra("id", noteId);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), AddObservationActivity.class);
                startActivity(intent);
            }
        });
    }

    // Exit app when back button is pressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
