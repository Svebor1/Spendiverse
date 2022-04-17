package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrikazTroskovaActivity extends AppCompatActivity {
    private final String TAG = "PrikazTroskovaActivity";
    private PieChart chart;
    private Spinner spinner;
    private Context context;
    private Spinner spinnerZaValute;
    private Button novaKategorijaTroskaButton;
    private Button novaValutaButton;
    private final Calendar myCalendar = Calendar.getInstance();
    String vremenskaRazdoblja[] = {"ukupno", "dan", "tjedan", "mjesec", "godina"};
    String[] zadaneValute = {"HRK", "USD", "EUR", "GBP"};
    ArrayList<String> valute = new ArrayList<>(Arrays.asList(zadaneValute));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_troskova);
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);


        spinner = findViewById(R.id.vremensko_razdoblje);
        spinnerZaValute = findViewById(R.id.spinner_valute);
        novaKategorijaTroskaButton = findViewById(R.id.nova_kategorija_troska);
        novaValutaButton = findViewById(R.id.nova_valuta);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, vremenskaRazdoblja);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        ArrayAdapter arrayAdapterValute = new ArrayAdapter(this, android.R.layout.simple_spinner_item, valute);
        arrayAdapterValute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZaValute.setAdapter(arrayAdapterValute);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                nadiTroskove(spinner.getSelectedItem().toString(), spinnerZaValute.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                nadiTroskove("ukupno", spinnerZaValute.getSelectedItem().toString());
            }
        });

        spinnerZaValute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                nadiTroskove(spinner.getSelectedItem().toString(), spinnerZaValute.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                nadiTroskove("ukupno", spinnerZaValute.getSelectedItem().toString());
            }

        });


        chart = findViewById(R.id.chart);

        context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                "dark_mode", Context.MODE_PRIVATE);
        int defaultValue = getResources().getInteger(R.integer.zadani_status_dark_modea);
        int darkModeStanje = sharedPref.getInt("dark_mode", defaultValue);
        if (darkModeStanje==0){
            chart.setHoleColor(Color.WHITE);//boja rupe u sredini
            chart.getLegend().setTextColor(Color.BLACK);//boja texta u legendi
        }else{
            chart.setHoleColor(Color.BLACK);//boja rupe u sredini
            chart.getLegend().setTextColor(Color.WHITE);//boja texta u legendi
        }

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false); //da se ne pojavljuje naziv kategorije na grafu
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f); //koeficijent trenja za animaciju okretanja grafa
        chart.setDrawHoleEnabled(true); //rupa u sredini
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f); //radijus rupe u sredini
        chart.setTransparentCircleRadius(61f);



        chart.setDrawCenterText(true); //omogućuje ispis naziva grafa u sredini rupe
        chart.setCenterText("Troškovi"); //naziv grafa u sredini grafa
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true); //omogućuje animaciju okretanja grafa
        chart.setHighlightPerTapEnabled(true);
        chart.getLegend().setWordWrapEnabled(true); //legenda s nazivima kategorija ne prelazi izvan ekrana
        Button unosTroskovaButton;
        Button vidjetiDetalje;
        Button financijskiPlanButton;
        vidjetiDetalje = findViewById(R.id.vidjeti_detalje);
        unosTroskovaButton = findViewById(R.id.unos_troskova);
        financijskiPlanButton = findViewById(R.id.financijski_plan);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unosTroskova();
            }
        };
        unosTroskovaButton.setOnClickListener(listener);

        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vidjetiDetalje();
            }
        };
        vidjetiDetalje.setOnClickListener(listener2);

        View.OnClickListener listener3 = new  View.OnClickListener() {
            @Override
            public void onClick(View v){
                financijskiPlan();
            }
        };
        financijskiPlanButton.setOnClickListener(listener3);

        View.OnClickListener listener4 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                novaKategorijaTroska();
            }
        };
        novaKategorijaTroskaButton.setOnClickListener(listener4);
        novaValutaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                novaValuta();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        nadiValute();
        nadiTroskove(spinner.getSelectedItem().toString(), spinnerZaValute.getSelectedItem().toString());
    }

    private void unosTroskova() {
        Intent intent = new Intent(this, UnosTroskovaActivity.class);
        startActivity(intent);
    }
    private void vidjetiDetalje() {
        Intent intent = new Intent(this, VidjetiDetaljeActivity.class);
        startActivity(intent);
    }
    private void financijskiPlan() {
        Intent intent = new Intent(this, FinancijskiPlanActivity.class);
        startActivity(intent);
    }
    private void novaKategorijaTroska() {
        Intent intent = new Intent(this, DodavanjeKategorijeTroska.class);
        startActivity(intent);
    }
    private void novaValuta() {
        Intent intent = new Intent(this, DodavanjeValuteActivity.class);
        startActivity(intent);
    }
    private boolean provjeriDatum(String razdoblje, int dan, int mjesec, int godina) {
        int godinaDanas = myCalendar.get(Calendar.YEAR);
        int mjesecDanas = myCalendar.get(Calendar.MONTH)+1;
        int danDanas = myCalendar.get(Calendar.DAY_OF_MONTH);
        long datum = LocalDate.of(godina, mjesec, dan).toEpochDay();
        long datumDanas = LocalDate.of(godinaDanas, mjesecDanas, danDanas).toEpochDay();
        if (razdoblje == "dan") {
            return dan == danDanas && mjesec == mjesecDanas && godina == godinaDanas;
        }
        else if (razdoblje == "tjedan") {
            return datumDanas-datum < 7;

        }
        else if (razdoblje == "mjesec") {
            return (godinaDanas == godina && mjesecDanas==mjesec)
                    || (godinaDanas == godina && mjesecDanas-mjesec == 1 && danDanas <= dan)
                    || (godinaDanas-godina == 1 && mjesecDanas == 1 && mjesec == 12 && danDanas <= dan);
        }
        else if (razdoblje == "godina") {
            return godinaDanas == godina
                    || godinaDanas-godina == 1 && mjesecDanas<mjesec
                    || godinaDanas-godina == 1 && mjesecDanas == mjesec && danDanas <= dan;
        }
        else {
            return true;
        }
    }
    private void  nadiTroskove(String razdoblje, String odabranaValuta) {
        ArrayList<Trosak> troskovi = new ArrayList<>();
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
                                String valuta = document.getData().get("valuta").toString();
                                Integer datumDan = Integer.parseInt(document.getData().get("datumDan").toString());
                                Integer datumMjesec = Integer.parseInt(document.getData().get("datumMjesec").toString());
                                Integer datumGodina = Integer.parseInt(document.getData().get("datumGodina").toString());
                                Integer cijena = Integer.parseInt(document.getData().get("cijena").toString());
                                String firebaseId = document.getId();
                                if (provjeriDatum(razdoblje, datumDan, datumMjesec, datumGodina) && valuta.equals(odabranaValuta)) {
                                    troskovi.add(new Trosak(naziv, datumDan, datumMjesec, datumGodina, kategorija, cijena, valuta, firebaseId));
                                }
                            }
                            List<PieEntry> entries = new ArrayList<>();
                            Map<String, Integer> trosakZaKategorije = new HashMap<>();
                            for(Trosak trosak: troskovi) {
                                Integer kategorijaDoSada = trosakZaKategorije.get(trosak.getKategorija());
                                if (kategorijaDoSada == null) {
                                    kategorijaDoSada = 0;
                                }
                                trosakZaKategorije.put(trosak.getKategorija(), kategorijaDoSada+trosak.getCijena());
                            }
                            for (Map.Entry<String, Integer> entry : trosakZaKategorije.entrySet()) {
                                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                            }
                            PieDataSet set = new PieDataSet(entries, "Troškovi");
                            final int[] mojeBoje = {
                                    Color.rgb(230, 25, 75), Color.rgb(60, 180, 75), Color.rgb(255, 225, 25),
                                    Color.rgb(0, 130, 200), Color.rgb(245, 130, 48), Color.rgb(145, 30, 180),
                                    Color.rgb(70, 240, 240), Color.rgb(240, 50, 230), Color.rgb(210, 245, 60),
                                    Color.rgb(250, 190, 212), Color.rgb(0, 128, 128), Color.rgb(220, 190, 255),
                                    Color.rgb(170, 110, 40), Color.rgb(255, 250, 200), Color.rgb(128, 0, 0),
                                    Color.rgb(170, 255, 195), Color.rgb(128, 128, 0), Color.rgb(255, 215, 180),
                                    Color.rgb(0, 0, 128), Color.rgb(128, 128, 128)
                            }; //postavljanje niza s bojama
                            set.setColors(mojeBoje);
                            PieData data = new PieData(set);
                            chart.setData(data);
                            chart.invalidate(); // refresh

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void nadiValute() {
        valute = new ArrayList<>(Arrays.asList(zadaneValute));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Context context = this;
        db.collection("korisnici").document(firebaseUser.getUid()).collection("valute")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String nazivValute = document.getData().get("naziv").toString();
                                valute.add(nazivValute);

                            }
                            ArrayAdapter arrayAdapterValute = new ArrayAdapter(context, android.R.layout.simple_spinner_item, valute);
                            arrayAdapterValute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerZaValute.setAdapter(arrayAdapterValute);
                            // prikaziTroskove(poredajPo.getSelectedItem().toString(), filterKategorija.getSelectedItem().toString(), filterzaRazdoblja.getSelectedItem().toString(), filterzaValute.getSelectedItem().toString());
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

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
}