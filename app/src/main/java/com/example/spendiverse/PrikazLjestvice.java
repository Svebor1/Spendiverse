package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class PrikazLjestvice extends AppCompatActivity {

    private final String TAG = "PrikazLjestvice";
    ArrayList<RezultatNatjecatelja> rezultatiNatjecatelja = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_ljestvice);
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ljestvica")
                .whereEqualTo("prikaz", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        rezultatiNatjecatelja = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("bodovi") != null && doc.get("nadimak") != null ) {
                                String[] bedzevi;
                                //vađenje bedzeva iz baze
                                String popisBedzeva = doc.getString("bedzevi");
                                if (popisBedzeva == null){
                                    popisBedzeva = "";
                                }
                                bedzevi = popisBedzeva.split(" ");
                                //vađenje iz baze rezultata natjecatelja
                                RezultatNatjecatelja trenutniRezultat = new RezultatNatjecatelja(doc.getString( "nadimak"), doc.getLong( "bodovi").intValue(),bedzevi);
                                //dodavanje rezultata natjecatelja u listu
                                rezultatiNatjecatelja.add(trenutniRezultat);
                            }

                        }
                        //komparator za sortiranje rezultata natjecatelja po bodovima
                        Comparator<RezultatNatjecatelja> usporediPoBodovima =
                                (RezultatNatjecatelja o1, RezultatNatjecatelja o2) -> o1.getRezultatKorisnika().compareTo(o2.getRezultatKorisnika());
                        //sortiranje liste uz pomoć komparatora
                        Collections.sort(rezultatiNatjecatelja, usporediPoBodovima.reversed());
                        upisiMjesta();
                        prikaziLjestvicu();
                    }
                });


    }
    private void upisiMjesta() {
        Integer brojac; //broji količinu prethodnih uzastopnih korisnika s istim brojem bodova
        Integer prosliBodovi = null; //bodovi prošlog natjecatelja
        brojac = 0; //u samom početku je 0 uzastopnih korisnika s istim brojem bodova
        Integer pozicija = 0;
        for(RezultatNatjecatelja rezultatNatjecatelja: rezultatiNatjecatelja) {
            Integer bodovi = rezultatNatjecatelja.getRezultatKorisnika(); //bodovi natjecatelja
            pozicija++;
            if (prosliBodovi != null) {
                if (bodovi.equals(prosliBodovi)) {
                    //ako je broj bodova sadašnjeg i prošlog natjecatelja isti
                    rezultatNatjecatelja.setPozicija(pozicija-brojac);
                    brojac++;
                }
                else {
                    //ako su broj bodova sadašnjeg i prošlog natjecatelja različiti
                    rezultatNatjecatelja.setPozicija(pozicija);
                    brojac = 1;
                }
            }
            else {
                //ako je to trenutni natjecatelj prvi u listi i nema prošlog
                rezultatNatjecatelja.setPozicija(pozicija);
                brojac = 1;
            }
            prosliBodovi = bodovi;
        }
    }

    /**
     * metoda za prikazivanje ljestvice
     */
    private void prikaziLjestvicu() {
        ListView prikazLjestvice = findViewById(R.id.ljestvica); //pronalazak ljestvicu prema id
        //stvaranje adaptera za ljestvicu
        LjestvicaAdapter arrayAdapter = new LjestvicaAdapter(this,R.id.ljestvica, rezultatiNatjecatelja);
        //postavljanje adaptera za ljestvicu
        prikazLjestvice.setAdapter(arrayAdapter);
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
}