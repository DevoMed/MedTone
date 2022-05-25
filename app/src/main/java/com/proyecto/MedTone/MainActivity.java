package com.proyecto.MedTone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    DatabaseReference dbr = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage str = FirebaseStorage.getInstance();

    private boolean checkPermission = false;
    ProgressDialog progressDialog;

    ListView listView;
    List<String> songsNameList;
    List<String> songsUrlList;
    List<String> songsArtistList;
    List<String> songsDurationList;
    ListAdapter adapter;

    JcPlayerView jcPlayerView;
    List<JcAudio> jcAudios;
    List<String> thumbnail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //Loading screen until all the songs are fetched
        //Pantalla de carga hasta que se cargan todas las canciones
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage("Paciencia...");

        listView = findViewById(R.id.songsList);
        songsNameList = new ArrayList<>();
        songsUrlList = new ArrayList<>();
        songsArtistList = new ArrayList<>();
        songsDurationList = new ArrayList<>();
        thumbnail = new ArrayList<>();

        jcAudios = new ArrayList<>();
        jcPlayerView = findViewById(R.id.jcplayer);

        toneFetch();

        //Click function for the songs to stream them
        //Hacer clic en la función para transmitir las canciones
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                jcPlayerView.playAudio(jcAudios.get(i));
                jcPlayerView.setVisibility(View.VISIBLE);
                jcPlayerView.createNotification();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void toneFetch() {

        //Fetching the songs, cover arts, duration from database
        //Obteniendo las canciones, portadas, duración de la base de datos
        dbr.child("songs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String name = ds.child("Name").getValue(String.class);
                    //Log.i("songname",name);

                    String artist = ds.child("Artist").getValue(String.class);
                    //Log.i("deejay", artist);

                    String duration = ds.child("Duration").getValue(String.class);
                    //Log.i("timetaken", duration);

                    String songurl = ds.child("SongURL").getValue(String.class);
                    //Log.i("link", songurl);

                    String imageurl = ds.child("ImageURL").getValue(String.class);
                    //Log.i("pic", imageurl);

                    songsNameList.add(name);
                    songsUrlList.add(songurl);
                    songsArtistList.add(artist);
                    songsDurationList.add(duration);
                    thumbnail.add(imageurl);

                    jcAudios.add(JcAudio.createFromURL(name, songurl));
                }
                //Update the listView and the songs to the player list
                //Actualiza la listView y las canciones a la lista de reproductión
                adapter = new ListAdapter(getApplicationContext(), songsNameList, thumbnail, songsArtistList, songsDurationList);
                jcPlayerView.initPlaylist(jcAudios, null);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                //Remove the loading screen
                //Elimina la pantalla de carga
                progressDialog.dismiss();
            }

            //In case of some error
            //En caso de algún error
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error....!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}