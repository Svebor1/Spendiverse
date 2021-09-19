package com.example.coinsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PrikazTroskovaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_troskova);
        Button unosTroskovaButton;
        unosTroskovaButton = findViewById(R.id.unos_troskova);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unosTroskova();
            }
        };
        unosTroskovaButton.setOnClickListener(listener);
    }
    private void unosTroskova() {
        Intent intent = new Intent(this, UnosTroskovaActivity.class);
        startActivity(intent);
    }
}