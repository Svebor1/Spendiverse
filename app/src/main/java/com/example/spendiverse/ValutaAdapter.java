package com.example.spendiverse;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ValutaAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> dataModalArrayList;
    public ValutaAdapter(@NonNull Context context, ArrayList<String> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
        this.context = context;
        this.dataModalArrayList = dataModalArrayList;
    }
    String TAG = "ValutaAdapter";
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.valuta_adapter_item, parent, false);
        }

        String valuta = getItem(position);
        TextView naziv = listitemView.findViewById(R.id.naziv_valute);
        naziv.setText(valuta);

        ImageButton kanta = listitemView.findViewById(R.id.kanta_valuta);
        if (position < 4) {
            //kategorije prehrana, kućanstvo i promet se ne mogu uređivati niti brisati
            kanta.setVisibility(View.INVISIBLE);
        }
        AlertDialog alertDialogBrisanje =
                //ako korisnik hoće izbrisati kategoriju prvo će se otvoriti prozor za potvrdu
                new AlertDialog.Builder(context)
                        .setTitle("Brisanje valute")
                        .setMessage("Jeste li sigurni da želite izbrisati valutu?" +
                                "\nBiti će izbrisani svi troškovi koji pripadaju toj valuti")
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                izbrisiValutu(valuta); //ako je korisnik potvrdio brisanje kategorije poziva se metoda brisanja kategorije
                            }
                        })
                        .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_baseline_help_24)
                        .create();

        kanta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBrisanje.show();
            }
        });
        return listitemView;
    };



    /**
     * metoda za brisanje kategorije
     * @param valuta
     */
    void izbrisiValutu(String valuta) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //brišemo troškove s zadanom kategorijom u bazi
        db.collection("korisnici").document(firebaseUser.getUid())
                .collection("troskovi")
                .whereEqualTo("valuta", valuta) //biramo samo troškove s zadanom kategorijom
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot trosak: task.getResult()) {
                                        //brisanje troškova
                                        trosak.getReference().delete();
                                    }
                                }
                            }
                        }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        //brišemo kategoriju u bazi
        db.collection("korisnici").document(firebaseUser.getUid())
                .collection("valute")
                .whereEqualTo("naziv", valuta) //biramo samo zadanu kategoriju
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot valuta: task.getResult()) {
                                        //brisanje kategorije
                                        valuta.getReference().delete();
                                    }
                                }
                            }
                        }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        remove(valuta); //brisanje kategorije u listview
    }
}
