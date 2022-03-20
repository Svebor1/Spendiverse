package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrikazTroskovaActivity extends AppCompatActivity {
    private final String TAG = "PrikazTroskovaActivity";
    private PieChart chart;
    private Spinner spinner;
    private final Calendar myCalendar = Calendar.getInstance();
    String vremenskaRazdoblja[] = {"ukupno", "dan", "tjedan", "mjesec", "godina"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_troskova);
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        spinner = findViewById(R.id.vremensko_razdoblje);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, vremenskaRazdoblja);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                nadiTroskove(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                nadiTroskove("ukupno");
            }

        });

        chart = findViewById(R.id.chart);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);
        chart.setCenterText("Troškovi");
        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        nadiTroskove(spinner.getSelectedItem().toString());
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
    //
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
    private void  nadiTroskove(String razdoblje) {
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
                                if (provjeriDatum(razdoblje, datumDan, datumMjesec, datumGodina)) {
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
                            set.setColors(ColorTemplate.COLORFUL_COLORS);
                            PieData data = new PieData(set);
                            chart.setData(data);
                            chart.invalidate(); // refresh

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