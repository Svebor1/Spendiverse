package com.example.coinsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Carousel;

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

                return false;
            }
        });

        //postavljanje slide showa
        //PlutoView slideShow = findViewById(R.id.slider_view);
        List <SlideModel> slideovi = new ArrayList<>();
        slideovi.add(new SlideModel("štednja"));
        slideovi.add(new SlideModel("ulaganje"));
        CarouselView carousel = findViewById(R.id.carouselView);
        carousel.setPageCount(2);
        ViewListener listener = new ViewListener() {
            @Override
            public View setViewForPosition(int position) {
                View customView = getLayoutInflater().inflate(R.layout.kartica_layout, null);
                TextView opis = customView.findViewById(R.id.opis);
                opis.setText(slideovi.get(position).getText());

                return customView;
            }
        };
        //SlideShowAdapter slideShowAdapter = new SlideShowAdapter(slideovi);
        //slideShow.create(slideShowAdapter, getLifecycle());
    }
} 