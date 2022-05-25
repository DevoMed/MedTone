package com.proyecto.MedTone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    EditText username, pass;
    CardView btn;
    TextView reg;

    DatabaseReference dbr = FirebaseDatabase.getInstance().getReference();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        username = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.passwordd);
        btn = (CardView) findViewById(R.id.login_card);
        reg = (TextView) findViewById(R.id.register_text);
    }
    //Function to login
    //Función para iniciar sesión
    public void UserLogin (View view) {

        final String userTxt = username.getText().toString();
        final String passTxt = pass.getText().toString();

        if(userTxt.isEmpty() || passTxt.isEmpty()) Toast.makeText(this, "Por favor, rellena todos los campos!",Toast.LENGTH_SHORT).show();

        else {

            dbr.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.hasChild(userTxt)) {

                        String stored  = snapshot.child(userTxt).child("password").getValue(String.class);
                        String hash_entry = encryptPass(passTxt);

                        if (hash_entry.equals(stored)) {
                            Toast.makeText(LoginActivity.this, "Bienvenido a MedTone!!!",Toast.LENGTH_SHORT).show();

                            Intent i1 = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i1);
                            finish();
                        }

                        else Toast.makeText(LoginActivity.this, "Contraseña incorrecta, intentalo de nuevo",Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(LoginActivity.this, "Usuario incorrecto, intentalo de nuevo", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

        }
    }
    //Function for clicking on the register button
    //Función para hacer clic en el botón de registro
    public void UserSignup (View view) {
        Intent i1 = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i1);

    }
    //Hashing the input to compare with the stored password of the user in the database, same function found in RegisterActivity
    //Encriptando la entrada para compararla con la contraseña del usuario almacenada en la base de datos, la misma función que se encuentra en RegisterActivity
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


