package com.example.coinsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Carousel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FinancijskaPismenostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financijska_pismenost);
        ExpandableListView lista;
        ExpandableListAdapter expandableListAdapter;
        List<String> kategorije;
        kategorije = new ArrayList<String>();
        kategorije.add("Lagano");
        kategorije.add("Srednje");
        kategorije.add("Teško");
        HashMap<String, List<String>> podkategorije;
        lista = (ExpandableListView) findViewById(R.id.expandableListView);
        podkategorije = spremnikKategorija.getData();
        expandableListAdapter = new CustomExpandableListAdapter(this, kategorije, podkategorije);
        lista.setAdapter(expandableListAdapter);
        lista.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String nazivGrupe = kategorije.get(groupPosition);
                String nazivTeme = podkategorije.get(nazivGrupe).get(childPosition);
                prikazTeme(nazivTeme, nazivGrupe.toLowerCase(), childPosition);
                return false;
            }
        });

    }
    private void prikazTeme(String nazivTeme, String nazivGrupe, int redniBrojKviza) {
        Bundle bundle = new Bundle();
        bundle.putString("nazivTeme", nazivTeme);
        bundle.putString("nazivGrupe", nazivGrupe);
        bundle.putInt("redniBrojKviza", redniBrojKviza);
        Intent intent = new Intent(this, Tema.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

} 