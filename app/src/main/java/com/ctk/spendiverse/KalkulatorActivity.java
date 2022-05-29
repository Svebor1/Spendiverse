package com.ctk.spendiverse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class KalkulatorActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private TabLayout tabovi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalkulator);
        tabovi = findViewById(R.id.tabovi);
        viewPager2 = findViewById(R.id.view_pager);
        TabAdapter tabAdapter = new TabAdapter(this);
        viewPager2.setAdapter(tabAdapter);
        tabovi.setTabGravity(TabLayout.GRAVITY_FILL);
        String[] imenaTabova = {"Kamate", "Popusti"};
        new TabLayoutMediator(tabovi, viewPager2,
                (tab, position) -> tab.setText(imenaTabova[position])
        ).attach();
    }
}