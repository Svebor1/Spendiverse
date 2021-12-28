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
        HashMap<String, List<String>> podkategorije;
        lista = (ExpandableListView) findViewById(R.id.expandableListView);
        podkategorije = spremnikKategorija.getData();
        kategorije = new ArrayList<String>(podkategorije.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, kategorije, podkategorije);
        lista.setAdapter(expandableListAdapter);
        lista.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                prikazTeme();
                return false;
            }
        });

    }
    private void prikazTeme() {
        Intent intent = new Intent(this, Tema.class);
        startActivity(intent);
    }

} 