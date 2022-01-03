package com.example.memorableplaces;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> places = new ArrayList<>();
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "com.example.memorableplaces", Context.MODE_PRIVATE);

        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();

        places.clear(); latitudes.clear(); longitudes.clear(); locations.clear();

        try {
            places = (ArrayList<String>) ObjectSerializer.deserialize(
                    sharedPreferences.getString("places",
                            ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(
                    sharedPreferences.getString("lats",
                            ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(
                    sharedPreferences.getString("longs",
                            ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(places.size() > 0 && latitudes.size() > 0 && longitudes.size() >0){
            if(places.size() == latitudes.size() && places.size() == longitudes.size()){
                for(int i=0; i< latitudes.size(); i++){
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i))));
                }
            }
        } else{
            places.add("Add a new place...");
            locations.add(new LatLng(0, 0));
        }

        if(places.size() == locations.size() && places.size() == 0) {

        }


        ListView listView = findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("placeNumber", position);

                startActivity(intent);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    return false;
                }
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure you want to delete this location?")
                        .setTitle("Delete this location")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                places.remove(position);
                                locations.remove(position);
                                arrayAdapter.notifyDataSetChanged();

                                try {
                                    ArrayList<String> latitudes = new ArrayList<>();
                                    ArrayList<String> longitudes = new ArrayList<>();
                                    for (LatLng coord: MainActivity.locations){
                                        latitudes.add(Double.toString(coord.latitude));
                                        longitudes.add(Double.toString(coord.longitude));
                                    }
                                    sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.places)).apply();
                                    sharedPreferences.edit().putString("lats", ObjectSerializer.serialize(latitudes)).apply();
                                    sharedPreferences.edit().putString("longs", ObjectSerializer.serialize(longitudes)).apply();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton("No", null).show();


                return true;
            }
        });




    }
}