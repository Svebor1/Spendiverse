package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
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
    Button brisanjeRacuna;
    String TAG = "MojProfil";
    private String nadimak;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_profil);
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        rjeseniKvizovi = findViewById(R.id.rjeseni_kvizovi);
        bodovi = findViewById(R.id.bodovi);
        prikazLjestvice = findViewById(R.id.prikaz_ljestvice);
        emailKorisnika = findViewById(R.id.email_korisnika);
        nadimakKorisnika = findViewById(R.id.nadimak_korisnika);
        editNadimka = findViewById(R.id.edit_nadimka);
        prikazNaLjestvici = findViewById(R.id.switch1);
        brisanjeRacuna = findViewById(R.id.brisanje_racuna);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        AlertDialog alertDialogBrisanje =
                new AlertDialog.Builder(this)
                        .setTitle("Brisanje profila")
                        .setMessage("Jeste li sigurni da želite izbrisati profil?")
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                izbrisiRacun();
                            }
                        })
                        .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_baseline_help_24)
                        .create();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBrisanje.show();
            }
        };
        brisanjeRacuna.setOnClickListener(listener);
        if (firebaseUser != null) {
            // Name, email address, and profile photo Url
            String email = firebaseUser.getEmail();
            emailKorisnika.setText(email.toString());
        }

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

        View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziLjestvicu();
            }
        };
        prikazLjestvice.setOnClickListener(listener3);
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
                nadimakKorisnika.setText(nadimak);
                db.collection("ljestvica").document(firebaseUser.getUid()).update("nadimak", nadimak);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void izbrisiRacun() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("korisnici").document(user.getUid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        db.collection("ljestvica").document(user.getUid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "User account deleted.");
                                startActivity(new Intent(MojProfil.this, MainActivity.class));
                                Toast.makeText(MojProfil.this, "Vaš profil je izbrisan", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }

    }

}