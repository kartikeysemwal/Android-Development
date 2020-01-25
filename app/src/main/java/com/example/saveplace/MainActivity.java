package com.example.saveplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> places = new ArrayList<String>();
    static ArrayList<String> latitude = new ArrayList<String>();
    static ArrayList<String> longitude = new ArrayList<String>();
    static ArrayAdapter arrayAdapter;
    static Boolean firsttime;
    Boolean appCalledFirstTime;
    static SharedPreferences sharedPreferences;

    public static void function(){
        //Toast.makeText(MainActivity.this,"This run", Toast.LENGTH_SHORT).show();
        sharedPreferences.edit().putBoolean("app", false).apply();
        try {
            sharedPreferences.edit().putString("title", ObjectSerializer.serialize(places)).apply();
            Log.i("Object",ObjectSerializer.serialize(places));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            sharedPreferences.edit().putString("latitude", ObjectSerializer.serialize(latitude)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            sharedPreferences.edit().putString("longitude", ObjectSerializer.serialize(longitude)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
        firsttime = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firsttime = true;
        appCalledFirstTime = true;

        sharedPreferences = getSharedPreferences( "com.example.saveplace", Context.MODE_PRIVATE);

        appCalledFirstTime = sharedPreferences.getBoolean("app",true);

        //Toast.makeText(MainActivity.this,"Hello" + appCalledFirstTime, Toast.LENGTH_SHORT).show();

        ListView listView = findViewById(R.id.listView);

        if(firsttime && appCalledFirstTime) {
            places.add("Add new place");
        }

        if(firsttime && !appCalledFirstTime){
            try {
                places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("title", ObjectSerializer.serialize(new ArrayList<String>())));
                //Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                Log.i("Done",places.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                latitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitude", ObjectSerializer.serialize(new ArrayList<String>())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                longitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitude", ObjectSerializer.serialize(new ArrayList<String>())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "You clicked " + places.get(i), Toast.LENGTH_SHORT).show();
                if(i==0){
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                }
                if(i>0){
                    Intent intent = new Intent(getApplicationContext(), MapsActivityToDisplayLocation.class);
                    intent.putExtra("latitude" , latitude.get(i-1));
                    intent.putExtra("longitude", longitude.get(i-1));
                    intent.putExtra("address", places.get(i));
                    startActivity(intent);
                }
            }
        });
    }

}

