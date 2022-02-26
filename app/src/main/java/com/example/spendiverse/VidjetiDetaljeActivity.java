package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VidjetiDetaljeActivity extends AppCompatActivity {
    private final String TAG = "VidjetiDetaljeActivity";
    ArrayList<Trosak> troskovi;
    private Spinner poredajPo;
    private  Spinner filterKategorija;
    private Spinner filterzaRazdoblja;
    String[] poredajPoArray = {"datumu uzlazno", "datumu silazno", "cijeni silazno", "cijeni uzlazno"};
    String[] kategorije = {"sve kategorije", "prehrana", "promet", "kućanstvo"};
    String[] vremenskoRazdoblje = {"ukupno", "dan", "tjedan", "mjesec", "godina"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vidjeti_detalje);
        poredajPo = findViewById(R.id.poredaj_po_spinner);
        ArrayAdapter poredajPoAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, poredajPoArray);
        poredajPo.setAdapter(poredajPoAdapter);
        poredajPo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                prikaziTroskove(poredajPo.getSelectedItem().toString(), filterKategorija.getSelectedItem().toString(), filterzaRazdoblja.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                prikaziTroskove("datumu uzlazno", "sve kategorije", "ukupno");

            }

        });
        filterKategorija = findViewById(R.id.prikaz_za_kategoriju);
        ArrayAdapter filterKategorijaAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, kategorije);
        filterKategorija.setAdapter(filterKategorijaAdapter);

        filterzaRazdoblja = findViewById(R.id.prikaz_za_razdoblje);
        ArrayAdapter filterRazdobljeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, vremenskoRazdoblje);
        filterzaRazdoblja.setAdapter(filterRazdobljeAdapter);

        filterKategorija.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                prikaziTroskove(poredajPo.getSelectedItem().toString(), filterKategorija.getSelectedItem().toString(), filterzaRazdoblja.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                prikaziTroskove("datumu uzlazno", "sve kategorije", "ukupno");

            }

        });
        filterzaRazdoblja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                prikaziTroskove(poredajPo.getSelectedItem().toString(), filterKategorija.getSelectedItem().toString(), filterzaRazdoblja.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                prikaziTroskove("datumu uzlazno", "sve kategorije", "ukupno");

            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        troskovi = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String naziv = document.getData().get("naziv").toString();
                                String kategorija = document.getData().get("kategorija").toString();
                                Integer datumDan = Integer.parseInt(document.getData().get("datumDan").toString());
                                Integer datumMjesec = Integer.parseInt(document.getData().get("datumMjesec").toString());
                                Integer datumGodina = Integer.parseInt(document.getData().get("datumGodina").toString());
                                Integer cijena = Integer.parseInt(document.getData().get("cijena").toString());
                                String firebaseId = document.getId();
                                troskovi.add(new Trosak(naziv, datumDan, datumMjesec, datumGodina, kategorija, cijena, firebaseId));
                            }
                            prikaziTroskove(poredajPo.getSelectedItem().toString(), filterKategorija.getSelectedItem().toString(), filterzaRazdoblja.getSelectedItem().toString());
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

    }
    private boolean provjeriVrijeme(Integer dan, Integer mjesec, Integer godina, String uvjetFiltriranjaZaDatum) {
        final Calendar myCalendar = Calendar.getInstance();
        int godinaDanas = myCalendar.get(Calendar.YEAR);
        int mjesecDanas = myCalendar.get(Calendar.MONTH)+1;
        int danDanas = myCalendar.get(Calendar.DAY_OF_MONTH);
        long datum = LocalDate.of(godina, mjesec, dan).toEpochDay();
        long datumDanas = LocalDate.of(godinaDanas, mjesecDanas, danDanas).toEpochDay();
        if (uvjetFiltriranjaZaDatum == "ukupno") {
            return true;
        }
        else {
            if (uvjetFiltriranjaZaDatum == "dan") {
                return danDanas==dan && godinaDanas==godina && mjesecDanas==mjesec;
            }
            else if (uvjetFiltriranjaZaDatum == "tjedan") {
                return datumDanas-datum < 7;
            }
            else if (uvjetFiltriranjaZaDatum == "mjesec") {
                return (godinaDanas == godina && mjesecDanas==mjesec)
                        || (godinaDanas == godina && mjesecDanas-mjesec == 1 && danDanas <= dan)
                        || (godinaDanas-godina == 1 && mjesecDanas == 1 && mjesec == 12 && danDanas <= dan);
            }
            else {
                return godinaDanas == godina
                        || godinaDanas-godina == 1 && mjesecDanas<mjesec
                        || godinaDanas-godina == 1 && mjesecDanas == mjesec && danDanas <= dan;
            }

        }
    }
    private boolean provjeriKategoriju(String kategorija, String uvjetFiltriranjaKategorija) {
        return kategorija.equals(uvjetFiltriranjaKategorija) || uvjetFiltriranjaKategorija.equals("sve kategorije");
    }
    private void prikaziTroskove(String uvjetSortiranja, String uvjetFiltriranjaKategorija, String uvjetFiltriranjaZadatum) {
        ArrayList<Trosak> filtriraniTroskovi = new ArrayList<>();
        List<Trosak> pomocna = troskovi.stream()
                .filter(t -> provjeriKategoriju(t.getKategorija(), uvjetFiltriranjaKategorija) && provjeriVrijeme(t.getDatumDan(), t.getDatumMjesec(), t.getDatumGodina(), uvjetFiltriranjaZadatum)).collect(Collectors.toList());
        filtriraniTroskovi.addAll(pomocna);
        Comparator<Trosak> usporediPoDatumu = new Comparator<Trosak>() {
            @Override
            public int compare(Trosak o1, Trosak o2) {
                if (!o1.getDatumGodina().equals(o2.getDatumGodina())) {
                    return o1.getDatumGodina().compareTo(o2.getDatumGodina());
                }
                else {
                    if (!o1.getDatumMjesec().equals(o2.getDatumMjesec())) {
                        return o1.getDatumMjesec().compareTo(o2.getDatumMjesec());
                    }
                    else {
                        return o1.getDatumDan().compareTo(o2.getDatumDan());
                    }
                }
            }
        };
        Comparator<Trosak> usporediPoCijeni = new Comparator<Trosak>() {
            @Override
            public int compare(Trosak o1, Trosak o2) {
                return o1.getCijena().compareTo(o2.getCijena());
            }
        };

        if (uvjetSortiranja.equals("datumu silazno")) {
            Collections.sort(filtriraniTroskovi, usporediPoDatumu);
        }
        else if (uvjetSortiranja.equals("datumu uzlazno")) {
            Collections.sort(filtriraniTroskovi, usporediPoDatumu);
            Collections.reverse(filtriraniTroskovi);
        }
        else if (uvjetSortiranja.equals("cijeni silazno")) {
            Collections.sort(filtriraniTroskovi, usporediPoCijeni);
        }
        else {
            Collections.sort(filtriraniTroskovi, usporediPoCijeni);
            Collections.reverse(filtriraniTroskovi);
        }

        ListView prikazTroskova = findViewById(R.id.prikazTroskova);
        TrosakAdapter arrayAdapter = new TrosakAdapter(this, filtriraniTroskovi);
        prikazTroskova.setAdapter(arrayAdapter);
    }
}