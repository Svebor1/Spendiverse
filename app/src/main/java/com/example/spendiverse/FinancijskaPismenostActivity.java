package com.example.spendiverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FinancijskaPismenostActivity extends AppCompatActivity {
    private String TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financijska_pismenost);
        //postavlja strelicu za natrag
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        List<String> rjeseniKvizovi = new ArrayList<String>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("rezultati_kvizova")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                rjeseniKvizovi.add(document.getId());
                            }
                            postavljanjeNoveExpandableListe(rjeseniKvizovi);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void postavljanjeNoveExpandableListe(List<String> rjeseniKvizovi){
        ExpandableListView lista;
        ExpandableListAdapter expandableListAdapter;
        int emoji = 0x1F31F;
        String zvjezdica = new String(Character.toChars(emoji));
        List<String> kategorije;
        kategorije = new ArrayList<String>();
        kategorije.add("Lagano" + zvjezdica) ;
        kategorije.add("Srednje"+ zvjezdica+zvjezdica);
        kategorije.add("Teško" + zvjezdica + zvjezdica + zvjezdica);
        HashMap<String, List<String>> podkategorije;
        lista = (ExpandableListView) findViewById(R.id.expandableListView);
        podkategorije = spremnikKategorija.getData();
        expandableListAdapter = new CustomExpandableListAdapter(this, kategorije, podkategorije,rjeseniKvizovi);
        lista.setAdapter(expandableListAdapter);
        lista.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String nazivGrupe = kategorije.get(groupPosition);
                String nazivTeme = podkategorije.get(nazivGrupe).get(childPosition);
                prikazTeme(nazivTeme, nazivGrupe.replace(zvjezdica,"").toLowerCase(), childPosition);
                return false;
            }
        });
    }



} 