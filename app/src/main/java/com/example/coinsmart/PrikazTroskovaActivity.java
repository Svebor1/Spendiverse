package com.example.coinsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PrikazTroskovaActivity extends AppCompatActivity {
    private final String TAG = "PrikazTroskovaActivity";
    private PieChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_troskova);
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
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(18.5f, "Prehrana"));
        entries.add(new PieEntry(26.7f, "Kucanstvo"));
        entries.add(new PieEntry(24.0f, "Zabava"));
        entries.add(new PieEntry(30.8f, "Putovanja"));
        PieDataSet set = new PieDataSet(entries, "Troškovi");
        List<Integer> colors = new ArrayList<>();
        colors.add(new Integer(Color.BLUE));
        colors.add(new Integer(Color.RED));
        colors.add(new Integer(Color.GREEN));
        colors.add(new Integer(Color.YELLOW));
        set.setColors(colors);

        PieData data = new PieData(set);
        chart.setData(data);
        chart.invalidate(); // refresh
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

}