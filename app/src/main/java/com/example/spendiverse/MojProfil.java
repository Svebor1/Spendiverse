package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    Switch ukljuciDarkMode;
    ImageButton editNadimka;
    Button brisanjeRacuna;
    ImageView bedzTrosak;
    ImageView planiranjeBedz;
    ImageView laganiKvizoviBedz;
    ImageView srednjiKvizoviBedz;
    ImageView teskiKvizoviBedz;
    Integer prviTrosakBedz;
    Integer prviPlanBedz;
    Integer postojanjeBedzaLaganihKvizova;
    Integer postojanjeBedzaSrednjihKvizova;
    Integer postojanjeBedzaTeskihKvizova;
    Context context;
    String TAG = "MojProfil";
    private String nadimak;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moj_profil);
        context = this;
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
        bedzTrosak = findViewById(R.id.trosak_bedz);
        laganiKvizoviBedz = findViewById(R.id.lagano_bedz);
        srednjiKvizoviBedz = findViewById(R.id.srednje_bedz);
        teskiKvizoviBedz = findViewById(R.id.tesko_bedz);
        planiranjeBedz = findViewById(R.id.financijski_plan_bedz);
        prikazNaLjestvici = findViewById(R.id.switch1);
        ukljuciDarkMode = findViewById(R.id.switch_dark_mode);
        brisanjeRacuna = findViewById(R.id.brisanje_racuna);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPref = context.getSharedPreferences(
                "dark_mode", Context.MODE_PRIVATE);
        int defaultValue = getResources().getInteger(R.integer.zadani_status_dark_modea);
        int darkModeStanje = sharedPref.getInt("dark_mode", defaultValue);
        ukljuciDarkMode.setChecked(darkModeStanje!=0);

        AlertDialog alertDialogBrisanje =
                //ako korisnik ho??e izbrisati profil prvo ??e se otvoriti prozor za potvrdu
                new AlertDialog.Builder(this)
                        .setTitle(R.string.brisanje_profila_naslov_alert)
                        .setMessage(R.string.brisanje_profila_alert)
                        .setPositiveButton(R.string.odgovor_da, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                izbrisiRacun(); //ako je korisnik potvrdio brisanje profila poziva se metoda brisanja profila
                            }
                        })
                        .setNegativeButton(R.string.odgovor_ne, new DialogInterface.OnClickListener() {
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
            } //korisnik je odabrao brisanje profila
        };
        brisanjeRacuna.setOnClickListener(listener);
        if (firebaseUser != null) {
            // dobivanje E-mail-a korisnika
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
                        //dobivanje podataka za ljestvicu iz baze te prikazivanje tih podataka na ekranu
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
                promjenaNadimka(); //pozivanje metode za promijenu nadimka u slu??aju ako je korisnik odabrao promijenu nadimka
            }
        };
        editNadimka.setOnClickListener(listener2);
        prikazNaLjestvici.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //ako je korisnik odlu??io promijeniti postavku za prikaz na ljestvici
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                db.collection("ljestvica").document(firebaseUser.getUid())
                    .update("prikaz", isChecked)
                        //ako je postavka uspje??no promijenjena spaja se na bazu i sprema ta promijena
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
            } //ako je odabran prikaz ljestvice
        };
        prikazLjestvice.setOnClickListener(listener3);
        provjeriPostojanjeBedzeva();
        procitajRezultate();

        ukljuciDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //ukljucen night mode
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //uga??en night mode
                }
                SharedPreferences sharedPref = context.getSharedPreferences(
                        "dark_mode", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (b){
                    editor.putInt("dark_mode", 1);
                }else{
                    editor.putInt("dark_mode", 0);
                }
                editor.apply();
            }
        });



    }


    /**
     * metoda koja slu??i za promijenu nadimka i spremanje novog nadimka u bazu
     */
    private void promjenaNadimka() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.unesite_novi_nadimak_alert);

        //postavljanje unosa
        final EditText input = new EditText(this);
        //postavljanjje tipa unosa na text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        //postavljanje gumba
        builder.setPositiveButton(R.string.nadimak_odgovor_potvrdi, null);
        builder.setNegativeButton(R.string.nadimak_odgovor_odustani, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().replace(" ","").length()==0){
                    input.setError("Nadimak ne smije biti prazan");
                }
                else{
                    if (input.getText().toString().length()>20){
                        input.setError("Nadimak mo??e imati najvi??e 20 znakova");
                    }
                    else{
                        nadimak = input.getText().toString();
                        nadimakKorisnika.setText(nadimak);
                        dialog.dismiss();
                    }
                }
                db.collection("ljestvica").document(firebaseUser.getUid()).update("nadimak", nadimak);

            }
        });

    }

    /**
     * prijelaz u PrikazLjestvice activity
     */
    private void prikaziLjestvicu() {
        Intent intent = new Intent(this, PrikazLjestvice.class);
        startActivity(intent);
    }

    /**
     * ova metoda izra??unava dobivene bodove za rije??ene kvizove
     */
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
    private void provjeriPostojanjeBedzeva() {
        prviTrosakBedz = 0;
        prviPlanBedz = 0;
        postojanjeBedzaLaganihKvizova = 0;
        postojanjeBedzaSrednjihKvizova = 0;
        postojanjeBedzaTeskihKvizova = 0;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("bedzevi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.getId().equals("prvi_trosak")) {
                                    prviTrosakBedz = 1;
                                }
                                if (document.getId().equals("prvi_plan")) {
                                    prviPlanBedz = 1;
                                }
                                if (document.getId().equals("bedz_lagani_kvizovi")) {
                                    postojanjeBedzaLaganihKvizova = 1;
                                }
                                if (document.getId().equals("bedz_srednji_kvizovi")) {
                                    postojanjeBedzaSrednjihKvizova = 1;
                                }
                                if (document.getId().equals("bedz_teski_kvizovi")) {
                                    postojanjeBedzaTeskihKvizova = 1;
                                }

                            }
                            if (prviTrosakBedz == 1){
                                bedzTrosak.setVisibility(View.VISIBLE);
                            } else{
                                bedzTrosak.setVisibility(View.GONE);
                            }
                            if (prviPlanBedz == 1){
                                planiranjeBedz.setVisibility(View.VISIBLE);
                            } else{
                                planiranjeBedz.setVisibility(View.GONE);
                            }
                            if (postojanjeBedzaLaganihKvizova == 1){
                                laganiKvizoviBedz.setVisibility(View.VISIBLE);
                            } else{
                                laganiKvizoviBedz.setVisibility(View.GONE);
                            }
                            if (postojanjeBedzaSrednjihKvizova == 1){
                                srednjiKvizoviBedz.setVisibility(View.VISIBLE);
                            } else{
                                srednjiKvizoviBedz.setVisibility(View.GONE);
                            }
                            if (postojanjeBedzaTeskihKvizova == 1){
                                teskiKvizoviBedz.setVisibility(View.VISIBLE);
                            } else{
                                teskiKvizoviBedz.setVisibility(View.GONE);
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Ova metoda ??ita sve rezultate korisnika iz baze.
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
                                //dodavanje rezultata kvizova u listu rezultati
                                rezultati.add(new Rezultat(nazivGrupe, nazivTeme,rezultat));
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                        izracunajRezultate(); //pozivanje metode za izra??unavanje svih bodova
                        brojRjesenihKvizova = rezultati.size();
                        rjeseniKvizovi.setText(brojRjesenihKvizova.toString());
                        bodovi.setText(brojBodova.toString()); //prikazivanje na ekranu sve bodove korisnika
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

    /**
     * ova metoda bri??e korisni??ki ra??un
     */
    private void izbrisiRacun() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                StorageReference slikaRef = storageRef.child(document.getId()+".jpg");
                                slikaRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                    }
                                });
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

        db.collection("korisnici").document(user.getUid())
                .delete()
                //brisanje svih podataka korisnika u bazi u collectionu korisnici
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
                //brisanje svih podataka korisnika u bazi u collectionu ljestvica
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
            user.delete() //brisanje korisni??kog ra??una
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "User account deleted.");
                                startActivity(new Intent(MojProfil.this, MainActivity.class));
                                Toast.makeText(MojProfil.this, "Va?? profil je izbrisan", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }

    }

}