package com.example.coinsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrikazTroskovaActivity extends AppCompatActivity {
    private final String TAG = "PrikazTroskovaActivity";
    private PieChart chart;
    private Spinner spinner;
    String vremenskaRazdoblja[] = {"dan", "tjedan", "mjesec", "godina", "ukupno"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_troskova);
        spinner = findViewById(R.id.vremensko_razdoblje);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, vremenskaRazdoblja);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
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
        nadiTroskove();
        Button unosTroskovaButton;
        Button vidjetiDetalje;
        TextView textView;
        textView = findViewById(R.id.textView);
        vidjetiDetalje = findViewById(R.id.vidjeti_detalje);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("korisnici").document(firebaseUser.getUid()).collection("troskovi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        textView.setText(textView.getText().toString() + document.getData().toString());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        unosTroskovaButton = findViewById(R.id.unos_troskova);
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
    }
    private void unosTroskova() {
        Intent intent = new Intent(this, UnosTroskovaActivity.class);
        startActivity(intent);
    }
    private void vidjetiDetalje() {
        Intent intent = new Intent(this, VidjetiDetaljeActivity.class);
        startActivity(intent);
    }
    private void  nadiTroskove() {
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
                                Integer datumDan = Integer.parseInt(document.getData().get("datumDan").toString());
                                Integer datumMjesec = Integer.parseInt(document.getData().get("datumMjesec").toString());
                                Integer datumGodina = Integer.parseInt(document.getData().get("datumGodina").toString());
                                Integer cijena = Integer.parseInt(document.getData().get("cijena").toString());
                                troskovi.add(new Trosak(naziv, datumDan, datumMjesec, datumGodina, kategorija, cijena));
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
}