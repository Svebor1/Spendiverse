package com.example.coinsmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class PrikazTroskovaActivity extends AppCompatActivity {
    private final String TAG = "PrikazTroskovaActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz_troskova);
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
            @Override //da
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