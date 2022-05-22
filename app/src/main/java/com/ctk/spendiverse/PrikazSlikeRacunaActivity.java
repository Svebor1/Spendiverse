package com.ctk.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PrikazSlikeRacunaActivity extends AppCompatActivity {
    private String imeSlikeRacuna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_slike_racuna);
        //postavljanje strelice natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        byte[] slikaRacuna = bundle.getByteArray("slika");
        imeSlikeRacuna = bundle.getString("imeSlike");
        ImageView slikaRacunaPrikaz = findViewById(R.id.slika_racuna_fullscreen);
        Bitmap slika = BitmapFactory.decodeByteArray(slikaRacuna,0,slikaRacuna.length);
        slikaRacunaPrikaz.setImageBitmap(slika);

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