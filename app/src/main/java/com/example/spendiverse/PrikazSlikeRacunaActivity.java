package com.example.spendiverse;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class PrikazSlikeRacunaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_slike_racuna);
        Bundle bundle = getIntent().getExtras();
        Bitmap slikaRacuna = bundle.getParcelable("slika");
        ImageView slikaRacunaPrikaz = findViewById(R.id.slika_racuna_fullscreen);
        slikaRacunaPrikaz.setImageBitmap(slikaRacuna);
    }
}