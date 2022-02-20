package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MojProfil extends AppCompatActivity {
    TextView rjeseniKvizovi;

    TextView bodovi;
    Integer brojBodova = 0;
    Integer brojRjesenihKvizova;
    Button prikazLjestvice;
    TextView emailKorisnika;
    TextView nadimakKorisnika;
    List<Rezultat> rezultati;
    Switch prikazNaLjestvici;
    ImageButton editNadimka;
    String TAG = "MojProfil";
    private String nadimak;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_profil);
        rjeseniKvizovi = findViewById(R.id.rjeseni_kvizovi);
        bodovi = findViewById(R.id.bodovi);
        prikazLjestvice = findViewById(R.id.prikaz_ljestvice);
        emailKorisnika = findViewById(R.id.email_korisnika);
        nadimakKorisnika = findViewById(R.id.nadimak_korisnika);
        editNadimka = findViewById(R.id.edit_nadimka);
        prikazNaLjestvici = findViewById(R.id.switch1);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String email = user.getEmail();
            emailKorisnika.setText(email.toString());
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        DocumentReference docRef = db.collection("ljestvica").document(firebaseUser.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        prikazNaLjestvici.setChecked(document.getData().get("prikaz").equals(true));
                        nadimak = document.getData().get("nadimak").toString();
                        nadimakKorisnika.setText(nadimak);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promjenaNadimka();
                nadimakKorisnika.setText(nadimak);
                db.collection("ljestvica").document(firebaseUser.getUid()).update("nadimak", nadimak);
            }
        };
        editNadimka.setOnClickListener(listener2);
        prikazNaLjestvici.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.collection("ljestvica").document(firebaseUser.getUid())
                    .update("prikaz", isChecked)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziLjestvicu();
            }
        };
        prikazLjestvice.setOnClickListener(listener);
        procitajRezultate();

    }
    private void promjenaNadimka() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Unesite novi nadimak");

        //postavljanje unosa
        final EditText input = new EditText(this);
        //postavljanjje tipa unosa na text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        //postavljanje gumba
        builder.setPositiveButton("potvrdi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nadimak = input.getText().toString();
            }
        });
        builder.setNegativeButton("odustani", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
    private void prikaziLjestvicu() {
        Intent intent = new Intent(this, PrikazLjestvice.class);
        startActivity(intent);
    }
    private void izracunajRezultate() {
        for (Rezultat rezultat : rezultati) {
            if (rezultat.getNazivGrupe().equals("lagano")) {
                brojBodova += rezultat.getRezultat()*10;
            }
            else if (rezultat.getNazivGrupe().equals("srednje")) {
                brojBodova += rezultat.getRezultat()*20;
            }
            else {
                brojBodova += rezultat.getRezultat()*30;
            }

        }
    }
    /**
     * Ova metoda čita sve rezultate korisnika iz baze.
     */
    private void procitajRezultate() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("rezultati_kvizova")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            rezultati = new ArrayList<Rezultat>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Integer rezultat = Integer.parseInt(document.getData().get("rezultat").toString());
                                String nazivGrupe = document.getData().get("naslov grupe").toString();
                                String nazivTeme = document.getData().get("naslov teme").toString();

                                rezultati.add(new Rezultat(nazivGrupe, nazivTeme,rezultat));
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                        izracunajRezultate();
                        brojRjesenihKvizova = rezultati.size();
                        rjeseniKvizovi.setText(brojRjesenihKvizova.toString());
                        bodovi.setText(brojBodova.toString());
                    }

                });
    }
}