package com.example.coinsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Tema extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tema);
        Button pocetakKviza;
        pocetakKviza=findViewById(R.id.kviz_start);
        pocetakKviza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prikazPitanja();
            }
        });
    }
    private void prikazPitanja() {
        Intent intent = new Intent(this, Pitanje.class);
        startActivity(intent);
    }
}