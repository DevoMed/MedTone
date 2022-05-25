package com.proyecto.MedTone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    EditText new_user;
    EditText new_pass;
    CardView regBtn;

    DatabaseReference dbr = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        new_user = (EditText) findViewById(R.id.new_user);
        new_pass = (EditText) findViewById(R.id.new_pass);
        regBtn = (CardView) findViewById(R.id.reg_card);
    }

    //Function to register
    //Función para registrarse
    public void SignUp(View view) {

        final String userTxt = new_user.getText().toString();
        final String passTxt = new_pass.getText().toString();


        if (userTxt.isEmpty() || passTxt.isEmpty())
            Toast.makeText(this, "Por favor, rellena todos los campos!", Toast.LENGTH_SHORT).show();

        else {

            String hashedTxt = encryptPass(passTxt);
            dbr.child("users").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.hasChild(userTxt)) Toast.makeText(RegisterActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                    else {
                        dbr.child("users").child(userTxt).child("password").setValue(hashedTxt);
                        Toast.makeText(RegisterActivity.this, "Bienvenido a MedTone!",Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
    //hashing the password to store it in the database
    //Método para encriptar la contraseña antes de guardarla en la base de datos
    private String encryptPass(String original) {

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(original.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);


            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}